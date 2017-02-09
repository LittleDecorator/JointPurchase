package com.acme.enums;

import com.acme.enums.serializer.OrderStatusDeserializer;
import com.acme.enums.serializer.OrderStatusSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.CaseFormat;

@JsonSerialize(using = OrderStatusSerializer.class)
@JsonDeserialize(using = OrderStatusDeserializer.class)
public enum OrderStatus {

    NEW("Новый","создан"),
    ACCEPTED("Принят", "добавлен в обработку"),
    IN_PROCESS("Обрабатывается","собирается"),
    READY("Собран","готов к выдаче"),
    DONE("Выполнен","выполнен"),
    CANCELED("Отменен","отменен");

    String text;
    String notifyText;

    OrderStatus(String text, String notifyText) {
        this.text = text;
        this.notifyText = notifyText;
    }

    public String getText() {
        return text;
    }

    public String getNotifyText() {
        return notifyText;
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
