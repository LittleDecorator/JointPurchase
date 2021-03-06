package com.acme.enums;

import com.acme.enums.serializer.ItemStatusDeserializer;
import com.acme.enums.serializer.ItemStatusSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.CaseFormat;

import javax.persistence.AttributeConverter;

@JsonSerialize(using = ItemStatusSerializer.class)
@JsonDeserialize(using = ItemStatusDeserializer.class)
public enum ItemStatus {

    AVAILABLE("В наличии"),
    AWAIT("Ожидается"),
    PREORDER("Под заказ"),
    SOLD_OUT("Продано");

    String text;

    ItemStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static ItemStatus getByName(String text){
        for(ItemStatus status : values()){
            if(status.name().equalsIgnoreCase(text) || CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, status.name()).equalsIgnoreCase(text)){
                return status;
            }
        }
        return null;
    }

    public static ItemStatus getByPosition(Integer pos){
        for(ItemStatus status : values()){
            if(status.ordinal()==pos){
                return status;
            }
        }
        return null;
    }
}
