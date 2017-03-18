package com.acme.enums.converters;

import com.acme.enums.OrderStatus;

import javax.persistence.AttributeConverter;

public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {
        @Override
        public String convertToDatabaseColumn(OrderStatus attribute) {
            String result = attribute!=null ? attribute.getText() : "FUCK";
            return result;
        }

        @Override
        public OrderStatus convertToEntityAttribute(String dbData) {
            return OrderStatus.getByName(dbData);
        }
    }