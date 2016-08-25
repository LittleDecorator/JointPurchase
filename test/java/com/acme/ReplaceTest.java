package com.acme;

public class ReplaceTest {

    public static void main(String args[]){
        String phone = "(926) 395 91 43";
        System.out.println(phone.replaceAll("\\p{P}",""));
        System.out.println(phone.replaceAll("[\\p{P}\\s+]",""));
        System.out.println(phone.replaceAll("\\s+",""));
    }

}
