package com.acme;

/**
 * Created by kobzev on 02.03.17.
 */
public class ArrayTest {

	public static void main(String[] args){
		String[][] mainFields = new String[][] { { "DOC_DATE", "DocDate" }, { "DOC_NUMBER", "DocNum" } };
		for (String[] f : mainFields) {
			System.out.println(f[0]);
			System.out.println(f[1]);
		}
	}

}
