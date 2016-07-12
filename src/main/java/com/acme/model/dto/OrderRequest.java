package com.acme.model.dto;

import com.acme.model.OrderItem;
import com.acme.model.PurchaseOrder;

import java.util.List;

public class OrderRequest {

    PurchaseOrder order;
    List<OrderItem> items;

    public PurchaseOrder getOrder() {
        return order;
    }

    public void setOrder(PurchaseOrder order) {
        this.order = order;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
                "order=" + order +
                ", items=" + items +
                '}';
    }
}
