package com.acme;

public class RefTest {

    public void doSome(int a, String b, Flee f){
        a = 2;
        b = "GLA";
        f.text = "FLA";
    }

    public static void main(String args[]){
        int a = 1;
        String b ="BLA";
        Flee f = new Flee();
        new RefTest().doSome(a, b, f);
        System.out.println(a);
        System.out.println(b);
        System.out.println(f.text);
    }

}

class Flee{

    String text = "DRO";

}