package com.acme;

public class ReplaceTest {

    public static void main(String args[]){
        String phone = "(926) 395 91 43";
        System.out.println(phone.replaceAll("\\p{P}",""));
        System.out.println(phone.replaceAll("[\\p{P}\\s+]",""));
        System.out.println(phone.replaceAll("\\s+",""));

//        String address = "Россия, 658971, Г МОСКВА, ЦАО, Москва, Менделеева, 562, 347";
//        String address = "Россия, 658971, Г МОСКВА, ЦАО, Москва, Менделеева, 562, ";
        String address = "Россия,,, , Москва, Менделеева, 562, ";
        System.out.println(address.replaceAll("\\s+",""));
        System.out.println(address.trim().replaceAll("[,\\s*]+,",",").replaceAll(",$", ""));
    }

}
