package com.acme.model.dto.mapper;

import com.acme.annotation.SimpleMapper;
import com.acme.model.Delivery;
import com.acme.model.Order;
import com.acme.model.OrderItem;
import com.acme.model.dto.OrderDto;
import com.acme.model.dto.OrderRequestDto;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.util.Sets;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import static java.util.stream.Collectors.joining;

@Mapper(componentModel = "spring")
public abstract class OrderMapper extends BaseMapper{

	@Mappings({
		@Mapping(target = "itemIds", expression = "java(collectItemIds(entity))"),
		@Mapping(target = "deliveryName", ignore = true),
		@Mapping(target = "deliveryId", expression = "java(findDeliveryId(entity))"),
		@Mapping(target = "recipientFullName", ignore = true),
	})
	public abstract OrderDto toDto(Order entity);

	@SimpleMapper
	@Mappings({
		@Mapping(target = "itemIds", ignore = true),
		@Mapping(target = "recipientAddress", ignore = true),
		@Mapping(target = "recipientFname", ignore = true),
		@Mapping(target = "recipientLname", ignore = true),
		@Mapping(target = "recipientEmail", ignore = true),
		@Mapping(target = "recipientPhone", ignore = true),
		@Mapping(target = "recipientMname", ignore = true),
		@Mapping(target = "comment", ignore = true),
		@Mapping(target = "closeOrderDate", ignore = true),
		@Mapping(target = "deliveryName", expression = "java(findDeliveryName(entity))"),
		@Mapping(target = "recipientFullName", expression = "java(buildFullName(entity))"),
	})
	public abstract OrderDto toSimpleDto(Order entity);

	@Mapping(target = "delivery", ignore = true)
	public abstract Order requestDtoToEntity(OrderRequestDto requestDto);

	public Set<OrderDto> toSimpleDto(Collection<Order> orders){
		Set<OrderDto> result = Sets.newHashSet();
		for (Order order : orders) {
			result.add(toSimpleDto(order));
		}
		return result;
	}

	/**
	 * Формирование полного имени получателя заказа
	 * @param order
	 * @return
	 */
	protected String buildFullName(Order order) {
		return Stream.of(order.getRecipientLname(), order.getRecipientFname(), order.getRecipientMname())
			.filter(s -> !Strings.isNullOrEmpty(s))
			.collect(joining(" "));
	}

	/**
	 *
	 * @param order
	 * @return
	 */
	protected String findDeliveryName(Order order){
		return initDelivery(order).getName();
	}

	protected String findDeliveryId(Order order){
		return initDelivery(order).getId();
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


	public Set<String> collectItemIds(Order entity){
		return initOrderItems(entity).stream().map(o -> o.getId().getItemId()).collect(Collectors.toSet());
	}

}
