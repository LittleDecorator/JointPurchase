package com.acme.model.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс представляет собой содержимое заказа.
 */
@Getter
@Setter
public class OrderRequest {

	private OrderRequestDto order;
	private List<OrderItemsList> items;

}
