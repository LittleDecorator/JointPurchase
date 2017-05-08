package com.acme.service.impl;

import com.acme.enums.OrderStatus;
import com.acme.model.Content;
import com.acme.model.Item;
import com.acme.model.ItemContent;
import com.acme.model.Order;
import com.acme.model.OrderItem;
import com.acme.model.dto.OrderItemsList;
import com.acme.model.dto.OrderRequest;
import com.acme.repository.ContentRepository;
import com.acme.repository.ItemContentRepository;
import com.acme.repository.ItemRepository;
import com.acme.repository.OrderItemRepository;
import com.acme.repository.OrderRepository;
import com.acme.service.EmailService;
import com.acme.service.ItemService;
import com.acme.service.OrderService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by kobzev on 20.12.16.
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

	@Autowired
	OrderRepository orderRepository;

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
	private PlatformTransactionManager transactionManager;

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
			return order;
		} catch (Exception ex) {
			log.error("Ошибка создания заказа", ex);
			transactionManager.rollback(status);
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
			} catch (Exception ex){
				log.error("Ошибка отмены заказа", ex);
				transactionManager.rollback(status);
			}
		} else {
			order = null;
		}
		return order;
	}

	@Override
	public void deleteOrder(String id) {
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		try{
			Order order = orderRepository.findOne(id);
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
			log.error("Ошибка удаления заказа", ex);
			transactionManager.rollback(status);
		}
	}
}
