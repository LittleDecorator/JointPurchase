package com.acme.model.dto;

import com.acme.model.Order;

import java.util.List;

/**
 * Класс представляет собой содержимое заказа.
 */
public class OrderRequest {

	private Order order;
	private List<OrderItemsList> items;

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public List<OrderItemsList> getItems() {
		return items;
	}

	public void setItems(List<OrderItemsList> items) {
		this.items = items;
	}
}
