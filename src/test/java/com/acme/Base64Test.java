package com.acme;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;

/**
 * Created by kobzev on 22.02.17.
 *
 */
public class Base64Test {

	private static final String HEXINDEX = "0123456789abcdef";

	public static void main(String[] args){
//		checkEQ();
		checkBASE("UTF-8");
//		checkHex();
	}

	private static byte[] hexToByte(String s) {
		int l = s.length() / 2;
		byte data[] = new byte[l];
		int j = 0;

		for (int i = 0; i < l; i++) {
			char c = s.charAt(j++);
			int n, b;
			n = HEXINDEX.indexOf(c);
			b = (n & 0xf) << 4;
			c = s.charAt(j++);
			n = HEXINDEX.indexOf(c);
			b += n & 0xf;
			data[i] = (byte) b;
		}
		return data;
	}

	private static void checkBASE(String charset){
//		String text = "zvLi5fLx8uLl7e376SDo8e/u6+3o8uXr/DogNTIwNSAgICAgICAgICAgICAgICAgICAgICAg ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICANCiAgICAgICAgICAgICAgICAgICAg ICAgICAgICAgICAgICAgICAgzsDOICLMyO3BIiAgICAgICAgICAgICAgICAgICAgICAgICAg ICAgICAgICAgICAgDQo0MDcwMjgxMDIwMDk5MDAwMDM3MiAgICAgICAgICAgICAgICAgICAg ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIA0KzeDo7OXt 7uLg7ejlIOrr6OXt8uA6IMfAytDb0s7FIMDK1sjOzcXQzc7FIM7B2cXR0sLOIsrO0M/O0MDW yN8gItLF1dHS0M7JLdAiICAgICAgICAgICANCs3g6Ozl7e7i4O3o5SDx9+Xy4Dogx8DOIsrO 0M/O0MDWyN8i0sXV0dLQzskt0CIgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg ICAgICAgDQrC++/o8ergIOfgIDEyLjAxLjIwMTUgICAgICAgICAgICAgICAgICAgICAgICAg ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIA0K8SAzMS4xMi4yMDE0 IMLVzsTf2cXFINHAy9zEzjogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgNKAy NDGgODYxLDUwIENSICAgICAgICANCtAv7u8gICBOIOTu6iAgICDK7uQg4eDt6uAgICAgICAg ICDR9y4g6u7w8OXx7y4gICAgICAgICAgIMTl4eXyICAgICAgICAgIMrw5eTo8iBOIO8gICAg DQogMSAgICAgICAgIDEgICAgMDQ0NTI1MjAyIDQwNzAyODEwMDAwNzAwNzIxMDI3ICAgICAg IDEwoDAwNCw2MyAgICAgICAgICAgICAgICAgNjUwMiAgIA0KIDEgICAgICAgICAyICAgIDA0 NDUyNTE4MSA0MDcwMjgxMDEwNTEwMDE0MjMzNiAgICAgICAxOaA4MjYsNDggICAgICAgICAg ICAgICAgIDY1MTAgICANCg0KyNLOw84g7+4g7uHu8O7y4OwgICAgICAgICAgICAgICAgICAg ICAgICAgICAgICAgICAgICAyOaA4MzEsMTEgICAgICAgICAgICAwLDAwICAgICAgICANCjEy LjAxLjIwMTUgyNHVzsTf2cXFINHAy9zEzjogICAgICAgICAgICAgICAgICAgICAgICAgICAg ICAgICAgICAgIDSgMjEyoDAzMCwzOSBDUiAgICAgDQrK7uvo9+Xx8uLuIOTu6vPs5e3y7uIg 7+4g5OXh5fLzOiAyICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg ICAgICAgICAgIA0Kyu7r6Pfl8fLi7iDk7urz7OXt8u7iIO/uIOrw5eTo8vM6IDAgICAgICAg ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICANCg0KDQoN Cg==";
		String text = "CjxIVE1MPjxCT0RZPlRFc3RpbmcgaGlzdG9yeTxicj48YnI-PGJyPtChINGD0LLQsNC20LXQvdC40LXQvCwg0J3QuNC60L7Qu9Cw0Lkg0JrQvtCx0LfQtdCyPC9CT0RZPjwvSFRNTD4K";

		byte[] result = Base64.isBase64(text) ? Base64.decodeBase64(text) : hexToByte(text);

//		System.out.println(new String (result, Charset.forName("windows-1251")));
		System.out.println(new String (result, Charset.forName(charset)));
	}

	private static void checkHex(){
		String text = "c7e0e2e5f0e5edede0ff20e2fbefe8f1eae02e20cde5eaeef0f0e5eaf2edeee520eef2eee1f0e0e6e5ede8e520eff0e820eff0eef1eceef2f0e520e820efe5f7e0f2e8";
		System.out.println(text.matches("^[0-9A-Fa-f]+$") ? "IS HEX" : "NOT HEX");
		System.out.println(text.matches("^[0-9A-Fa-f]{2}+$") ? "IS HEX" : "NOT HEX");
	}


	private static void checkEQ(){
		System.out.println("left outer".equalsIgnoreCase("left outer") ? "EQ":"NOT EQ");
	}
}
