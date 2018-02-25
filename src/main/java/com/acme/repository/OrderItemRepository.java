package com.acme.repository;

import com.acme.model.OrderItem;
import com.acme.model.embedded.OrderItemId;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by kobzev on 17.02.17.
 *
 *
 */
public interface OrderItemRepository extends CrudRepository<OrderItem, OrderItemId> {

	void deleteByIdOrderIdAndIdItemIdNotIn(String orderId, List<String> itemIdList);

	void deleteByIdOrderId(String orderId);

	void deleteByIdItemId(String itemId);

	@Transactional(noRollbackFor = {Exception.class, EmptyResultDataAccessException.class})
	List<String> deleteByIdOrderIdIn(List<String> orderIds);

	List<OrderItem> findAllByIdOrderId(String orderId);

	List<OrderItem> findAllByIdOrderIdIn(List<String> orderIds);

}