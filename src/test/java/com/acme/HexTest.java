package com.acme;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by nikolay on 21.02.17.
 *
 */
public class HexTest {

    private static final String HEXINDEX = "0123456789abcdef";
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    private final static String REPORT_DATA_ENCODING="windows-1251";

//    private static String test = "Заверенная выписка. Некорректное отображение при просмотре и печати";
    private static String hex = "zvLi5fLx8uLl7e376SDo8e/u6+3o8uXr/DogNTIwNSAgICAgICAgICAgICAgICAgICAgICAg&#10;ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICANCiAgICAgICAgICAgICAgICAgICAg&#10;ICAgICAgICAgICAgICAgICAgzsDOICLMyO3BIiAgICAgICAgICAgICAgICAgICAgICAgICAg&#10;ICAgICAgICAgICAgDQo0MDcwMjgxMDIwMDk5MDAwMDM3MiAgICAgICAgICAgICAgICAgICAg&#10;ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIA0KzeDo7OXt&#10;7uLg7ejlIOrr6OXt8uA6IMfAytDb0s7FIMDK1sjOzcXQzc7FIM7B2cXR0sLOIsrO0M/O0MDW&#10;yN8gItLF1dHS0M7JLdAiICAgICAgICAgICANCs3g6Ozl7e7i4O3o5SDx9+Xy4Dogx8DOIsrO&#10;0M/O0MDWyN8i0sXV0dLQzskt0CIgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg&#10;ICAgICAgDQrC++/o8ergIOfgIDEyLjAxLjIwMTUgICAgICAgICAgICAgICAgICAgICAgICAg&#10;ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIA0K8SAzMS4xMi4yMDE0&#10;IMLVzsTf2cXFINHAy9zEzjogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgNKAy&#10;NDGgODYxLDUwIENSICAgICAgICANCtAv7u8gICBOIOTu6iAgICDK7uQg4eDt6uAgICAgICAg&#10;ICDR9y4g6u7w8OXx7y4gICAgICAgICAgIMTl4eXyICAgICAgICAgIMrw5eTo8iBOIO8gICAg&#10;DQogMSAgICAgICAgIDEgICAgMDQ0NTI1MjAyIDQwNzAyODEwMDAwNzAwNzIxMDI3ICAgICAg&#10;IDEwoDAwNCw2MyAgICAgICAgICAgICAgICAgNjUwMiAgIA0KIDEgICAgICAgICAyICAgIDA0&#10;NDUyNTE4MSA0MDcwMjgxMDEwNTEwMDE0MjMzNiAgICAgICAxOaA4MjYsNDggICAgICAgICAg&#10;ICAgICAgIDY1MTAgICANCg0KyNLOw84g7+4g7uHu8O7y4OwgICAgICAgICAgICAgICAgICAg&#10;ICAgICAgICAgICAgICAgICAyOaA4MzEsMTEgICAgICAgICAgICAwLDAwICAgICAgICANCjEy&#10;LjAxLjIwMTUgyNHVzsTf2cXFINHAy9zEzjogICAgICAgICAgICAgICAgICAgICAgICAgICAg&#10;ICAgICAgICAgIDSgMjEyoDAzMCwzOSBDUiAgICAgDQrK7uvo9+Xx8uLuIOTu6vPs5e3y7uIg&#10;7+4g5OXh5fLzOiAyICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg&#10;ICAgICAgICAgIA0Kyu7r6Pfl8fLi7iDk7urz7OXt8u7iIO/uIOrw5eTo8vM6IDAgICAgICAg&#10;ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICANCg0KDQoN&#10;Cg==";

    public static void main(String[] args) throws DecoderException, UnsupportedEncodingException {
//        String hex = stringToHex(test);
//        System.out.println("HEX : " + hex);
        System.out.println("/* our method */");
        byte[] decodeData = hexToByte(hex);
        System.out.println(new String(decodeData, Charset.forName(REPORT_DATA_ENCODING)));
//        System.out.println(new String(decodeData, Charset.forName("UTF-8")));
        System.out.println("/* 16 bit method */");
//        decodeData = hexStringToByteArray(hex);
//        System.out.println(new String(decodeData, Charset.forName(REPORT_DATA_ENCODING)));
//        System.out.println(new String(decodeData, Charset.forName("UTF-8")));
        System.out.println("/* converter method */");
//        decodeData = DatatypeConverter.parseHexBinary(hex);
//        System.out.println(new String(decodeData, Charset.forName(REPORT_DATA_ENCODING)));
//        System.out.println(new String(decodeData, Charset.forName("UTF-8")));
        System.out.println("/* apache */");
        byte[] bytes = Hex.decodeHex(hex.toCharArray());
        System.out.println(new String(bytes, Charset.forName(REPORT_DATA_ENCODING)));
//        System.out.println(new String(bytes, Charset.forName("UTF-8")));
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

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private static String stringToHex(String input) throws UnsupportedEncodingException
    {
        if (input == null) throw new NullPointerException();
        return asHex(input.getBytes(Charset.forName(REPORT_DATA_ENCODING)));
    }

    private static String asHex(byte[] buf){
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i)
        {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }
}
