package com.acme.enums;

import com.acme.enums.serializer.NotificationTypeDeserializer;
import com.acme.enums.serializer.NotificationTypeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.CaseFormat;

/**
 * Created by nikolay on 09.05.17.
 */
@JsonSerialize(using = NotificationTypeSerializer.class)
@JsonDeserialize(using = NotificationTypeDeserializer.class)
public enum NotificationType {

    NORMAL("Простое"),
    ERROR("Ошибка");

    String text;

    NotificationType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static NotificationType getByName(String text){
        for(NotificationType type : values()){
            if(type.name().equalsIgnoreCase(text) || CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, type.name()).equalsIgnoreCase(text)){
                return type;
            }
        }
        return null;
    }

    public static NotificationType getByPosition(Integer pos){
        for(NotificationType type: values()){
            if(type.ordinal()==pos){
                return type;
            }
        }
        return null;
    }

}
