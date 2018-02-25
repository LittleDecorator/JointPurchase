package com.acme.model.dto.mapper;

import com.acme.model.Delivery;
import com.acme.model.Order;
import com.acme.model.OrderItem;
import com.acme.model.dto.OrderDto;
import com.acme.model.dto.OrderViewDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public abstract class OrderMapper extends BaseMapper{

	@Mappings({
		@Mapping(source = "subjectId", target = "recipientId"),
		@Mapping(source = "dateAdd", target = "createDate"),
		@Mapping(target = "recipientName", expression = "java(buildFullName(entity))"),
		@Mapping(target = "delivery", expression = "java(findDeliveryName(entity))")
	})
	public abstract OrderViewDto toViewDto(Order entity);

	@Mappings({
		@Mapping(target = "orderItems", expression = "java(initOrderItems(entity))"),
		@Mapping(target = "delivery", expression = "java(initDelivery(entity))")
	})
	public abstract OrderDto toDto(Order entity);

	public List<OrderViewDto> toViewDtoList(Collection<Order> orders){
		List<OrderViewDto> result = new ArrayList<>();
		for (Order order : orders) {
			result.add(toViewDto(order));
		}
		return result;
	}

	/**
	 * Формирование полного имени получателя заказа
	 * @param order
	 * @return
	 */
	protected String buildFullName(Order order) {
		return (order.getRecipientLname()  + " " +  order.getRecipientFname() + " " + order.getRecipientMname()).trim();
	}

	/**
	 *
	 * @param order
	 * @return
	 */
	protected String findDeliveryName(Order order){
		return initDelivery(order).getName();
	}

	/**
	 *
	 * @param order
	 * @return
	 */
	protected Delivery initDelivery(Order order){
		Hibernate.initialize(order.getDelivery());
		return deproxy(order.getDelivery(), Delivery.class);
	}

	/**
	 * Явная инициализация lazy объектов
	 * @param order
	 * @return
	 */
	protected List<OrderItem> initOrderItems(Order order){
		Hibernate.initialize(order.getOrderItems());
		return order.getOrderItems();
	}


}
