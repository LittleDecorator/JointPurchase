package com.acme;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.assertj.core.util.Lists;


public class MatcherTest {

    public static void main(String[] args){

        List<String> names = Lists.newArrayList("test.jpg", "test.jpg", "bla.png", "bla(1).png", "bla(1).png","bla (2).png", "fla.svg");

        Pattern p = Pattern.compile("(.*?)(\\..*)?");

        Map<String, Integer> fileNames = new HashMap<>();
        String result;



        for(String fileName : names){
            // проверяем наличие вложений с одинаковыми названиями
            fileNames.put(fileName, null == fileNames.get(fileName) ? 0 : (fileNames.get(fileName) + 1));
            Matcher m = p.matcher(fileName);
            if (m.matches()) {
                String prefix = m.group(1);
                String suffix = m.group(2);

                result = prefix;
                Integer copyNumber = null;

                if (fileNames.get(fileName) > 0) {
                    copyNumber =  fileNames.get(fileName);
                }

                if(copyNumber!=null){
                    result += "_copy(" + copyNumber + ")";
                }

                result += (suffix == null ? "" : suffix);
                System.out.println(result);
            }
        }

    }

}
