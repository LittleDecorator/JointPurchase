package com.acme.enums;

import com.google.common.base.CaseFormat;

public enum ItemStatus {

    SOLD_OUT("Продано"),
    SHIPPING("Ожидается"),
    IN_STOCK("В наличие");

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
                System.out.println(status);
                return status;
            }
        }
        return null;
    }
}
