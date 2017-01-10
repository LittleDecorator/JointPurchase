package com.acme.service;

import com.acme.model.PurchaseOrder;

import java.util.List;
import java.util.Map;

/**
 * Created by kobzev on 20.12.16.
 */
public interface OrderService {

	Map<String, Object> getOrderInfo(String orderId);
	List<PurchaseOrder> getOrders(String subjectId);

}