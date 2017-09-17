package com.acme.time;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * Created by nikolay on 17.09.17.
 */
public class TestTime {

    public static void main(String[] args){
        LocalDate localDate = LocalDate.now();
        long dayStart = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long dayEnd = localDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        System.out.println(dayStart);
        System.out.println(dayEnd);
        System.out.println(System.currentTimeMillis() - dayStart);
        System.out.println(dayEnd - System.currentTimeMillis());
        System.out.println(dayEnd - dayStart);
    }

}
