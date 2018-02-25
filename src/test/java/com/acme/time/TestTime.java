package com.acme.time;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

        LocalDateTime nowInSys = LocalDateTime.now(Clock.system(ZoneId.systemDefault()));
        LocalDateTime nowInUtc = LocalDateTime.now(Clock.systemUTC());
        LocalDateTime nowInMSK = LocalDateTime.now(Clock.system(ZoneId.of("Europe/Moscow")));
        System.out.println("SYS -> " +nowInSys);
        System.out.println("SYS millis -> " +nowInSys);
        System.out.println("UTC -> " +nowInUtc);
        System.out.println("UTC -> " +nowInUtc.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli());
        System.out.println("UTC millis -> " + Instant.now(Clock.systemUTC()).toEpochMilli());
        System.out.println("MSK -> " +nowInMSK);

        Instant instant = Instant.parse("2018-01-29T00:00:00.000Z");
        System.out.println("MILLIS SYS -> " + instant.toEpochMilli());
        System.out.println("MILLIS DEF -> " + instant.toEpochMilli());
        System.out.println("MILLIS UTC -> " + instant.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli());
        System.out.println("MILLIS MSK -> " + instant.atZone(ZoneId.of("Europe/Moscow")).toInstant().toEpochMilli());
    }


}
