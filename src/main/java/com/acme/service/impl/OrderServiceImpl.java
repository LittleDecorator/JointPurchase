package com.acme.service.impl;

import com.acme.model.Content;
import com.acme.model.Item;
import com.acme.model.ItemContent;
import com.acme.model.Order;
import com.acme.model.OrderItem;
import com.acme.repository.ContentRepository;
import com.acme.repository.ItemContentRepository;
import com.acme.repository.ItemRepository;
import com.acme.repository.OrderItemRepository;
import com.acme.repository.OrderRepository;
import com.acme.service.OrderService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by kobzev on 20.12.16.
 */
@Service
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
	public List<Order> getOrders(String subjectId) {
		return null;
	}
}
