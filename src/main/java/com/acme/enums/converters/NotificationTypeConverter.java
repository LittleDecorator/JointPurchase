package com.acme.enums.converters;

import com.acme.enums.NotificationType;

import javax.persistence.AttributeConverter;

public class NotificationTypeConverter implements AttributeConverter<NotificationType, String> {
        @Override
        public String convertToDatabaseColumn(NotificationType attribute) {
            return attribute.name();
        }

        @Override
        public NotificationType convertToEntityAttribute(String dbData) {
            return NotificationType.getByName(dbData);
        }
    }