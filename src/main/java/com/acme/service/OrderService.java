package com.acme.service;

import com.acme.model.Order;
import com.acme.model.dto.OrderItemsList;
import com.acme.model.dto.OrderRequest;
import com.acme.model.filter.OrderFilter;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by kobzev on 20.12.16.
 */
public interface OrderService {

	long genOrderNum();

	Page<Order> getAllOrders(OrderFilter filter, Pageable pageable);

	List<Order> getHistory(OrderFilter filter);

	List<Order> getCustomerOrders(String id);

	Order getOrder(String orderId);

	List<OrderItemsList> getOrderItems(String orderId);
	/**
	 * Получение информации о заказе
	 * @param orderId
	 * @return
     */
	Map<String, Object> getOrderInfo(String orderId);

	/**
	 * Создание нового заказа
	 * @param request
	 * @return
     */
	Order createOrder(OrderRequest request);

	/**
	 * Отмена заказа
	 * @param id
	 * @return
     */
	Order cancelOrder(String id);

	/**
	 * Удаление заказа
	 * @param id
     */
	void deleteOrder(String id);

}
