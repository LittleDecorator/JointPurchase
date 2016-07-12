package com.acme.enums;

import com.acme.util.OrderStatusDeserializer;
import com.acme.util.OrderStatusSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.CaseFormat;

@JsonSerialize(using = OrderStatusSerializer.class)
@JsonDeserialize(using = OrderStatusDeserializer.class)
public enum OrderStatus {

    NEW("Новый"),
    IN_PROCESS("Обрабатывается"),
    READY("Собран"),
    DONE("Выполнен"),
    CANCELED("Отменен");

    String text;

    OrderStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static OrderStatus getByName(String text){
        for(OrderStatus status : values()){
            if(status.name().equalsIgnoreCase(text) || CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, status.name()).equalsIgnoreCase(text)){
                System.out.println(status);
                return status;
            }
        }
        return null;
    }

}
