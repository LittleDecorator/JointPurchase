package com.acme.repository;

import com.acme.model.OrderItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by kobzev on 17.02.17.
 *
 *
 */
public interface OrderItemRepository extends CrudRepository<OrderItem, String> {

	void deleteByOrderIdAndItemIdNotIn(String orderId, List<String> itemIdList);

	void deleteByOrderId(String orderId);

	void deleteByItemId(String itemId);

	List<OrderItem> findAllByOrderId(String orderId);

}