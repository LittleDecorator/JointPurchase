package com.acme;

import com.google.common.collect.Lists;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by kobzev on 08.06.17.
 */
public class UtilTests {

	public static void main(String[] args){
		UtilTests tests = new UtilTests();
		tests.isStringInList();
		tests.generateSmsCode();
	}

	private void isStringInList(){
		String match = "send_nk";
		System.out.println(Lists.newArrayList("in_progress","in_progress_nk","end","end_nk").contains(match));
	}

	private void generateSmsCode() {
		int min = 0,max = 9, size=5;
		StringBuilder builder = new StringBuilder();
		while(size>0){
			builder.append(ThreadLocalRandom.current().nextInt(min, max + 1));
			size--;
		}
		System.out.println(Integer.parseInt(builder.toString()));
	}
}
