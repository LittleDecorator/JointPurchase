package com.acme;

import com.google.common.base.CaseFormat;

public class EnumTest {

    public static void main(String[] args){
        TaskViewer.getByName("obServer");
    }

    public enum TaskViewer {

        AUTHOR, EXECUTOR, OBSERVER;

        public static TaskViewer getByName(String text){
            for(TaskViewer status : values()){
                if(status.name().equalsIgnoreCase(text) || CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, status.name()).equalsIgnoreCase(text)){
                    return status;
                }
            }
            return null;
        }
    }

}
