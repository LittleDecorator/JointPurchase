package com.acme.enums.converters;

import com.acme.enums.ItemStatus;

import javax.persistence.AttributeConverter;

public class ItemStatusConverter implements AttributeConverter<ItemStatus, String> {
        @Override
        public String convertToDatabaseColumn(ItemStatus attribute) {
            return attribute.name();
        }

        @Override
        public ItemStatus convertToEntityAttribute(String dbData) {
            return ItemStatus.getByName(dbData);
        }
    }