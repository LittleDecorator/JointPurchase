package com.acme;

import com.google.common.base.Strings;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.assertj.core.util.Lists;


public class MatcherTest {

    public static void main(String[] args){
        System.out.println(String.valueOf(check(null)));
        System.out.println(String.valueOf(check("")));
        System.out.println(String.valueOf(check("blandroid")));
        System.out.println(String.valueOf(check("ios-45")));
        System.out.println(String.valueOf(check("_androiid")));
        System.out.println(String.valueOf(check("6|android5")));
    }

    public void old(){
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

    public static boolean check(String input){
        String platform = Strings.nullToEmpty(input);
        Pattern p = Pattern.compile("(?i)android|ios");
        Matcher m = p.matcher(platform);
        return m.find();
    }


}
