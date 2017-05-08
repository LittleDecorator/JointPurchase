package com.acme.service;

import com.acme.model.Order;
import com.acme.model.dto.OrderRequest;

import java.util.Map;

/**
 * Created by kobzev on 20.12.16.
 */
public interface OrderService {

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
