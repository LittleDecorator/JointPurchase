package com.acme.math;

/**
 * Created by kobzev on 06.07.17.
 */
public class MathTest {

	public static void main(String[] args){
		MathTest test = new MathTest();
		test.getDigits(-12345);
		test.getDigits(1022);
		test.getDigits(-534);
		test.getDigits(20);
		test.getDigits(3);
	}

//	private void getDigits(Integer input){
//		String number = String.valueOf(input);
//		char[] digits = number.toCharArray();
//		System.out.println(digits[0]);
//	}

//	private void getDigits(Integer input){
//		while (input > 0) {
//			System.out.println( input % 10);
//			input = input / 10;
//		}
//	}

	private void getDigits(Integer input){
		int digit = String.valueOf(Math.abs((long)input)).charAt(0) - '0';
		System.out.println(input < 0 ? 0 - digit: digit);
		System.out.println(input);
	}
}
