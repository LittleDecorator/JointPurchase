package com.acme;

import com.ibm.icu.text.Transliterator;

public class RussianToLatin {

        public static String RUSSIAN_TO_LATIN_BGN = "Russian-Latin/BGN";

        public static void main(String[] args) {
            String prepared = "Шелк 86*86";
            String company = "Crayon Rocks | Шелк";

            Transliterator transliterator0 = Transliterator.getInstance(RUSSIAN_TO_LATIN_BGN);
            String result0 = transliterator0.transliterate(company).replaceAll("·|ʹ|\\.|\"|,|\\(|\\)", "").replaceAll("\\*|\\s+","-").toLowerCase();
            System.out.println("Russian-Latin/BGN:" + result0);

            Transliterator transliterator = Transliterator.getInstance("Any-Latin/UNGEGN");
            String result = transliterator.transliterate(company).replaceAll("\\*|\\s+","-").toLowerCase();
            System.out.println("Any-Latin/UNGEGN:" + result);

            Transliterator transliterator1 = Transliterator.getInstance("Any-Latin/UNGEGN");
            String result1 = transliterator1.transliterate(company).replaceAll("\\*|\\s+","-").toLowerCase();
            System.out.println("Any-Latin/UNGEGN:" + result1);

            Transliterator transliterator2 = Transliterator.getInstance("Any-Latin/BGN");
            String result2 = transliterator2.transliterate(company).replaceAll("\\*|\\s+","-").toLowerCase();
            System.out.println("Any-Latin/BGN:" + result2);

            Transliterator transliterator3 = Transliterator.getInstance("Any-Latin/Names");
            String result3 = transliterator3.transliterate(company).replaceAll("\\*|\\s+","-").toLowerCase();
            System.out.println("Any-Latin/Names:" + result3);

            Transliterator transliterator4 = Transliterator.getInstance("Any-Latin");
            String result4 = transliterator4.transliterate(company).replaceAll("\\*|\\s+","-").toLowerCase();
            System.out.println("Any-Latin:" + result4);
        }


}
