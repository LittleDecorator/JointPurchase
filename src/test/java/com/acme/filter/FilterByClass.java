package com.acme.filter;

import com.google.common.collect.FluentIterable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kobzev on 09.08.17.
 */
public class FilterByClass {

	public static void main(String[] args){
		new FilterByClass().check();
	}

	public void check(){
		List<SignItem> items = Arrays.asList(new SignItem("first"),new CryptoSignItem("secord"));
		System.out.println(items.size());
		System.out.println(items.toString());
		List<CryptoSignItem> result = FluentIterable.from(items).filter(CryptoSignItem.class).toList();
		System.out.println(result.size());
		System.out.println(result.toString());
	}

	class SignItem {
		String name;

		public SignItem(String name) {
			this.name = name;
		}
	}

	class CryptoSignItem extends SignItem {
		public CryptoSignItem(String name) {
			super(name);
		}
	}
}
