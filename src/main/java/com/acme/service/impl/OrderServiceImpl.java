package com.acme.service.impl;

import com.acme.enums.OrderStatus;
import com.acme.model.*;
import com.acme.model.dto.OrderItemsList;
import com.acme.model.dto.OrderRequest;
import com.acme.model.filter.OrderFilter;
import com.acme.repository.*;
import com.acme.repository.specification.OrderViewSpecifications;
import com.acme.service.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

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

	private AtomicInteger seq = new AtomicInteger(0);

	@Autowired
	OrderRepository orderRepository;
	@Autowired
	OrderViewRepository orderViewRepository;
	@Autowired
	OrderItemRepository orderItemRepository;
	@Autowired
	ItemRepository itemRepository;
	@Autowired
	ItemContentRepository itemContentRepository;
	@Autowired
	ContentRepository contentRepository;
	@Autowired
	private ItemService itemService;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	JmsService jmsService;
	@Autowired
	private PlatformTransactionManager transactionManager;

	@Override
	public long genOrderNum() {
		LocalDate localDate = LocalDate.now();
		return Long.valueOf(""+ localDate.getDayOfMonth() + String.format("%02d",localDate.getMonthValue()) + String.format("%03d", seq.incrementAndGet()));
	}

	@Override
	public List<OrderView> getAllOrders(OrderFilter filter) {
		/* Сортировка видимо будет идти по модели, и в запросе не участвует */
		Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit(), Sort.Direction.DESC, "createDate");
		return Lists.newArrayList(orderViewRepository.findAll(OrderViewSpecifications.filter(filter), pageable).iterator());
	}

	@Override
	public List<OrderView> getHistory(OrderFilter filter) {
		Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit(), Sort.Direction.DESC, "createDate");
		return Lists.newArrayList(orderViewRepository.findAll(OrderViewSpecifications.filter(filter), pageable).iterator());
	}

	@Override
	public List<OrderView> getCustomerOrders(String id) {
		return orderViewRepository.findAll(OrderViewSpecifications.customer(id));
	}

	@Override
	public Order getOrder(String orderId) {
		return orderRepository.findOne(orderId);
	}

	@Override
	public List<OrderItemsList> getOrderItems(String orderId) {
		List<OrderItemsList> result = Lists.newArrayList();
		// получим заказ
		List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);
		List<Item> items = itemRepository.findByIdIn(orderItems.stream().map(OrderItem::getItemId).collect(Collectors.toList()));
		Map<String, Item> itemMap = items.stream().collect(Collectors.toMap(Item::getId, Function.identity()));
		result.addAll(orderItems.stream().map(orderItem -> new OrderItemsList(itemMap.get(orderItem.getItemId()), orderItem.getCount())).collect(Collectors.toList()));
		return result;
	}

	/**
	 * Получение полной информации по заказу
	 * @param orderId
	 * @return
     */
	public Map<String, Object> getOrderInfo(String orderId) {
		Map<String, Object> result = Maps.newHashMap();
		// получение информации о получателе и заказе
		Order order = orderRepository.findOne(orderId);
		result.put("order",order);
		List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);
		Map<String, OrderItem> orderItemsMap = orderItems.stream().collect(Collectors.toMap(OrderItem::getItemId, Function.identity()));
		// информация о количествах товара в заказе
		result.put("orderItems",orderItemsMap);
		List<Item> items = itemRepository.findByIdIn(orderItems.stream().map(OrderItem::getItemId).collect(Collectors.toList()));
		Map<String, Item> itemsMap = items.stream().collect(Collectors.toMap(Item::getId, Function.identity()));
		// товары заказа
		result.put("items", itemsMap);
		Map<String, Content> contentMap = Maps.newHashMap();
		for(String itemId : itemsMap.keySet()){
			ItemContent itemContent = itemContentRepository.findAllByItemId(itemId).stream().filter(ItemContent::isMain).findFirst().orElse(null);
			if(itemContent != null){
				contentMap.put(itemId, contentRepository.findOne(itemContent.getContentId()));
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
		try {
			/* сохраним заказ из запроса. */
			request.getOrder().setStatus(OrderStatus.NEW);
			// TODO: fix temporary hook
			request.getOrder().setRecipientAddress("Здесь должен быть адрес пункта самовывоза при типе доставки - САМОВЫВОЗ");
			// TODO: override order uid
			request.getOrder().setUid(genOrderNum());
			Order order = orderRepository.save(request.getOrder());

			/* добавим записи в таблицу связи заказ-товар */
			if(request.getItems() == null || request.getItems().isEmpty()){
				throw new IllegalArgumentException("NO items in request");
			}
			for (OrderItemsList itemsList : request.getItems()) {
				// собираем товар для дальнейшей обработки
				OrderItem orderItem = new OrderItem();
				orderItem.setItemId(itemsList.getItem().getId());
				orderItem.setOrderId(order.getId());
				orderItem.setCount(itemsList.getCount());
				orderItemRepository.save(orderItem);
				// изменить кол-во товара в наличие
				Item item = itemRepository.findOne(itemsList.getItem().getId());
				item.setInStock(item.getInStock() - itemsList.getCount());
				item.setInOrder(item.getInOrder() + itemsList.getCount());
				itemRepository.save(item);
			}
			//удаляем записи, где заказ совпадает, а товар нет.
			List<String> itemIdList = request.getItems().stream().map(OrderItemsList::getItem).map(Item::getId).collect(Collectors.toList());
			orderItemRepository.deleteByOrderIdAndItemIdNotIn(order.getId(), itemIdList);
			transactionManager.commit(status);
			// отправка оведомлений
			jmsService.orderConfirm(order);
			return order;
		} catch (Exception ex) {
			log.error("Ошибка создания заказа = {0}", request.getOrder(), ex);
			transactionManager.rollback(status);
			notificationService.sendErrorNotification(request.getOrder());
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
			orderItemRepository.deleteByOrderId(id);
			// удаляем заказ
			orderRepository.delete(id);
			transactionManager.commit(status);
		} catch (Exception ex) {
			log.error("Ошибка удаления заказа = {0}",order, ex);
			transactionManager.rollback(status);
		}
	}
}
