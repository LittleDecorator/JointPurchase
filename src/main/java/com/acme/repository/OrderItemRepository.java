package com.acme.repository;

import com.acme.model.Item;
import com.acme.model.OrderItem;
import com.acme.model.PurchaseOrder;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by kobzev on 17.02.17.
 *
 *
 */
public interface OrderItemRepository extends CrudRepository<OrderItem, String> {

	void deleteByOrderAndItemNotIn(PurchaseOrder order, List<Item> itemList);

	void deleteByOrder(PurchaseOrder order);

}