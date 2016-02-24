package com.acme.enums;

public enum Status {

    NEW("новый"),
    IN_PROCESS("обрабатывается"),
    READY("собран"),
    DONE("выполнен"),
    CANCELED("отменен");

    String text;

    Status(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    /*public Status valueOf(String text){
        for(Status status : Status.values()){
            if(status.getText().contentEquals(text)){
                return status;
            }
        }
        return null;
    }*/
}
