package com.acme.model.dto;

import com.acme.model.Item;

/**
 * Created by kobzev on 18.03.17.
 *
 * Класс представляет собой содержимое заказа.
 * Детали заказа убраны.
 *
 */
public class OrderItemsList {

	private Item item;
	private Integer count;

	public OrderItemsList() {}

	public OrderItemsList(Item item, Integer count) {
		this.item = item;
		this.count = count;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
