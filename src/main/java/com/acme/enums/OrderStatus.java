package com.acme.enums;

import com.acme.util.StatusSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

//@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonSerialize(using = StatusSerializer.class)
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
            if(status.name().equalsIgnoreCase(text)){
                return status;
            }
        }
        return null;
    }

}
