package com.acme.service.impl;

import com.acme.enums.OrderStatus;
import com.acme.model.*;
import com.acme.model.dto.OrderItemsList;
import com.acme.model.dto.OrderRequest;
import com.acme.model.dto.mapper.OrderMapper;
import com.acme.model.embedded.OrderItemId;
import com.acme.model.filter.OrderFilter;
import com.acme.repository.*;
import com.acme.repository.specification.OrderSpecifications;
import com.acme.service.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by kobzev on 20.12.16.
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

	private AtomicInteger seq;

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderItemRepository orderItemRepository;
	@Autowired
	private ItemRepository itemRepository;
	@Autowired
	private ItemContentRepository itemContentRepository;
	@Autowired
	private ContentRepository contentRepository;
	@Autowired
	private ItemService itemService;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	JmsService jmsService;
	@Autowired
	private PlatformTransactionManager transactionManager;
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private DeliveryRepository deliveryRepository;

	@Override
	public long genOrderNum() {
		LocalDate localDate = LocalDate.now();
		return Long.valueOf(""+ localDate.getDayOfMonth() + String.format("%02d",localDate.getMonthValue()) + String.format("%03d", seq.incrementAndGet()));
	}

	@Override
	public List<Order> getAllOrders(OrderFilter filter) {
		/* Сортировка видимо будет идти по модели, и в запросе не участвует */
		Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit(), Sort.Direction.DESC, "dateAdd");
		return orderRepository.findAll(OrderSpecifications.filter(filter), pageable).getContent();
	}

	@Override
	public List<Order> getHistory(OrderFilter filter) {
		Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit(), Sort.Direction.DESC, "dateAdd");
		return orderRepository.findAll(OrderSpecifications.filter(filter), pageable).getContent();
	}

	@Override
	public List<Order> getCustomerOrders(String id) {
		return orderRepository.findAll(OrderSpecifications.customer(id));
	}

	@Override
	public Order getOrder(String orderId) {
		return orderRepository.findOne(orderId);
	}

	@Override
	public List<OrderItemsList> getOrderItems(String orderId) {
		List<OrderItemsList> result = Lists.newArrayList();
		// получим заказ
		List<OrderItem> orderItems = orderItemRepository.findAllByIdOrderId(orderId);
		Set<Item> items = itemRepository.findAllByIdIn(orderItems.stream().map(oi -> oi.getId().getItemId()).collect(Collectors.toList()));
		Map<String, Item> itemMap = items.stream().collect(Collectors.toMap(Item::getId, Function.identity()));
		result.addAll(orderItems.stream().map(orderItem -> new OrderItemsList(itemMap.get(orderItem.getId().getItemId()), orderItem.getCount())).collect(Collectors.toList()));
		return result;
	}

	/**
	 * Получение полной информации по заказу
	 * @param orderId
	 * @return
     */
	@Transactional
	public Map<String, Object> getOrderInfo(String orderId) {
		Map<String, Object> result = Maps.newHashMap();
		// получение информации о получателе и заказе
		Order order = orderRepository.findOne(orderId);
		result.put("order",order);
		Map<String, OrderItem> orderItemsMap = order.getOrderItems().stream().collect(Collectors.toMap(oi -> oi.getId().getItemId(), Function.identity()));
		// информация о количествах товара в заказе
		result.put("orderItems",orderItemsMap);
		Set<Item> items = itemRepository.findAllByIdIn(order.getOrderItems().stream().map(oi -> oi.getId().getItemId()).collect(Collectors.toList()));
		Map<String, Item> itemsMap = items.stream().collect(Collectors.toMap(Item::getId, Function.identity()));
		// товары заказа
		result.put("items", itemsMap);
		Map<String, Content> contentMap = Maps.newHashMap();
		for(String itemId : itemsMap.keySet()){
			ItemContent itemContent = itemContentRepository.findAllByItemId(itemId).stream().filter(ItemContent::isMain).findFirst().orElse(null);
			if(itemContent != null){
				contentMap.put(itemId, contentRepository.findOne(itemContent.getContent().getId()));
			} else {
				contentMap.put(itemId, contentRepository.findOneByIsDefault(true));
			}

		}
		// изображения товаров
		result.put("contents", contentMap);
		return result;
	}

	@Override
	public Order createOrder(OrderRequest request) {
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		Order order = orderMapper.requestDtoToEntity(request.getOrder());
		try {
			order.setOrderItems(Lists.newArrayList());
			order.setDelivery(deliveryRepository.findOne(request.getOrder().getDelivery()));
			/* сохраним заказ из запроса. */
			order.setStatus(OrderStatus.NEW);
			// TODO: fix temporary hook
			if(Strings.isNullOrEmpty(request.getOrder().getRecipientAddress())){
				order.setRecipientAddress("Здесь должен быть адрес пункта самовывоза при типе доставки - САМОВЫВОЗ");
			}
			// TODO: override order uid
			order.setUid(genOrderNum());
			order = orderRepository.save(order);

			/* добавим записи в таблицу связи заказ-товар */
			if(request.getItems() == null || request.getItems().isEmpty()){
				throw new IllegalArgumentException("NO items in request");
			}
			for (OrderItemsList itemsList : request.getItems()) {
				// собираем товар для дальнейшей обработки
				OrderItem orderItem = new OrderItem();
				OrderItemId id = new OrderItemId(itemsList.getItem().getId(), order.getId());
				orderItem.setId(id);
				orderItem.setCount(itemsList.getCount());
				orderItem = orderItemRepository.save(orderItem);
				order.getOrderItems().add(orderItem);
				// изменить кол-во товара в наличие
				Item item = itemRepository.findOne(itemsList.getItem().getId());
				item.setInStock(item.getInStock() - itemsList.getCount());
				item.setInOrder(item.getInOrder() + itemsList.getCount());
				itemRepository.save(item);
			}
			//удаляем записи, где заказ совпадает, а товар нет.
			List<String> itemIdList = request.getItems().stream().map(OrderItemsList::getItem).map(Item::getId).collect(Collectors.toList());
			orderItemRepository.deleteByIdOrderIdAndIdItemIdNotIn(order.getId(), itemIdList);
			transactionManager.commit(status);
			// отправка оведомлений
			jmsService.orderConfirm(order);
			return order;
		} catch (Exception ex) {
			log.error("Ошибка создания заказа = {0}", request.getOrder(), ex);
			transactionManager.rollback(status);
			notificationService.sendErrorNotification(order);
			return null;
		}
	}

	@Override
	public Order cancelOrder(String id) {
		Order order = orderRepository.findOne(id);
		// получим заблокированные статусы
		List<OrderStatus> lockedStatuses = Lists.newArrayList(OrderStatus.CANCELED, OrderStatus.DONE);
		if(!lockedStatuses.contains(order.getStatus())){
			// изменим статус заказа если возможно
			TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
			try{
				// изменяем статус заказа на "ОТМЕНЕН"
				order.setStatus(OrderStatus.CANCELED);
				orderRepository.save(order);
				// обновим кол-во товара в наличие
				itemService.increaseCountByOrder(order.getId());
				transactionManager.commit(status);
				// отправка оведомлений
				jmsService.orderConfirm(order);
			} catch (Exception ex){
				log.error("Ошибка отмены заказа = {0}", order, ex);
				transactionManager.rollback(status);
				notificationService.sendErrorNotification(order);
			}
		} else {
			order = null;
		}
		return order;
	}

	@Override
	public void deleteOrder(String id) {
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		Order order = orderRepository.findOne(id);
		try{
			if(!order.getStatus().equals(OrderStatus.CANCELED)){
				// если удаляемый заказ небыл отменен, то необходимо обновить кол-во товара
				itemService.increaseCountByOrder(id);
			}
			// удаляем выбранные товары заказа
			orderItemRepository.deleteByIdOrderId(id);
			// удаляем заказ
			orderRepository.delete(id);
			transactionManager.commit(status);
		} catch (Exception ex) {
			log.error("Ошибка удаления заказа = {0}",order, ex);
			transactionManager.rollback(status);
		}
	}

	@PostConstruct
	public void initSequence() {
		String lastNumber = String.valueOf(orderRepository.getLastUid());
		int value =  Integer.parseInt(lastNumber.substring(lastNumber.length() - 3));
		seq = new AtomicInteger(value);
	}
}
