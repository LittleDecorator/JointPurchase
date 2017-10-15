package com.acme.service;

import com.acme.model.Order;
import com.acme.model.OrderView;
import com.acme.model.dto.OrderItemsList;
import com.acme.model.dto.OrderRequest;
import com.acme.model.filter.OrderFilter;

import java.util.List;
import java.util.Map;

/**
 * Created by kobzev on 20.12.16.
 */
public interface OrderService {

	long genOrderNum();

	List<OrderView> getAllOrders(OrderFilter filter);

	List<OrderView> getHistory(OrderFilter filter);

	List<OrderView> getCustomerOrders(String id);

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
