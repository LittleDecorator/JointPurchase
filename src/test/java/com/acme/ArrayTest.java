package com.acme;

/**
 * Created by kobzev on 02.03.17.
 */
public class ArrayTest {

	public static void main(String[] args){
//		String[][] mainFields = new String[][] { { "DOC_DATE", "DocDate" }, { "DOC_NUMBER", "DocNum" } };
//		for (String[] f : mainFields) {
//			System.out.println(f[0]);
//			System.out.println(f[1]);
//		}
		ArrayTest test = new ArrayTest();
		test.testLoop();
	}


	public void testLoop(){
		int total = 2321;
		int portionSize = 10;
		int portionCount = total/portionSize + (total%portionSize > 0 ? 1 : 0);
		int portionPos = 0;

		for(int i = 0; i < portionCount; i++){
			portionPos = i*portionSize;
			System.out.println(portionPos);
			System.out.println(portionPos + portionSize-1);
		}
		System.out.println("END");
	}
}
