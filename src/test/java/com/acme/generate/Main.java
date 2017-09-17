package com.acme.generate;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Main {
    private char[] A;
    private AtomicInteger seq = new AtomicInteger(0);

    private void init() {
        A = new char[] {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
        };
        System.out.println("digits = " + A.length);
    }

    private void generate(int length, String id) {
        if (length == 8) {
            System.out.println(id);
        } else {
            for (int i = 0; i < A.length; i++)
                generate(length + 1, id + A[i]);
        }
    }

    private int getNumber(){
        return seq.incrementAndGet();
    }

    public static void main(String[] args) {
        Main test = new Main();
//        test.init();
//        test.generate(0,  "");

        LocalDate localDate = LocalDate.now();
        IntStream.range(8, 30).forEach(i -> {
            System.out.println(""+ localDate.getDayOfMonth() + String.format("%02d",localDate.getMonthValue()) + String.format("%03d", test.getNumber()));
        });
    }
}
