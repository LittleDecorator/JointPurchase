package com.acme.repository;

import com.acme.model.OrderItem;
import com.acme.model.embedded.OrderItemId;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by kobzev on 17.02.17.
 *
 *
 */
public interface OrderItemRepository extends CrudRepository<OrderItem, OrderItemId> {

	void deleteByIdOrderIdAndIdItemIdNotIn(String orderId, List<String> itemIdList);

	@Modifying
	@Query("delete from OrderItem  where id.orderId = :orderId")
	void deleteByIdOrderId(@Param("orderId") String orderId);

	@Modifying
	@Query("delete from OrderItem  where id.itemId = :itemId")
	void deleteByIdItemId(@Param("itemId") String itemId);

	@Transactional(noRollbackFor = {Exception.class, EmptyResultDataAccessException.class})
	void deleteByIdOrderIdIn(List<String> orderIds);

	List<OrderItem> findAllByIdOrderId(String orderId);


}