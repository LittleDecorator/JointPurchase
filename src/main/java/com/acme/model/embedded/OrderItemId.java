package com.acme.model.embedded;

import com.acme.model.Item;
import com.acme.model.PurchaseOrder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * Created by kobzev on 17.02.17.
 *
 * Составной ключ таблицы связи между "PurchaseOrder" и "Item"
 */

@Embeddable
public class OrderItemId implements Serializable {

	@Column(name = "order_id")
	private String orderId;

	@Column(name = "item_id")
	private String itemId;

	public OrderItemId() {
	}

	public OrderItemId(String itemId, String orderId) {
		this.itemId = itemId;
		this.orderId = orderId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((itemId == null) ? 0 : itemId.hashCode());
		result = prime * result
				+ ((itemId == null) ? 0 : itemId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		OrderItemId other = (OrderItemId) obj;

		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;

		if (orderId == null) {
			if (other.orderId != null)
				return false;
		} else if (!orderId.equals(other.orderId))
			return false;

		return true;
	}
}
