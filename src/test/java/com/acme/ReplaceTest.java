package com.acme;

public class ReplaceTest {

    public static void main(String args[]){
//        String phone = "(926) 395 91 43";
//        System.out.println(phone.replaceAll("\\p{P}",""));
//        System.out.println(phone.replaceAll("[\\p{P}\\s+]",""));
//        System.out.println(phone.replaceAll("\\s+",""));
//
////        String address = "Россия, 658971, Г МОСКВА, ЦАО, Москва, Менделеева, 562, 347";
////        String address = "Россия, 658971, Г МОСКВА, ЦАО, Москва, Менделеева, 562, ";
//        String address = "Россия,,, , Москва, Менделеева, 562, ";
//        System.out.println(address.replaceAll("\\s+",""));
//        System.out.println(address.trim().replaceAll("[,\\s*]+,",",").replaceAll(",$", ""));
		ReplaceTest test = new ReplaceTest();
		B b = test.getB();
		b.printField();

    }

    public B getB(){
		return new B();
	}

    class A {
		private String field = "fla";

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}
	}

	class B extends A {

		public void printField(){
			System.out.println(super.getField());
			super.setField("Bla");
			System.out.println(super.getField());
		}
	}
}
