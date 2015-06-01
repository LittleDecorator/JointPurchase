import java.text.DecimalFormat;
import java.text.ParseException;

public class Resize {

    public static void main(String[] args) throws ParseException {

//        Integer width = 4720;
        Integer width = 1920;
//        Integer height = 2953;
        Integer height = 1200;
        boolean isWidthBigger = width>height;
        int nWidth = 0;

        System.out.println("bigger size -> "+(isWidthBigger?"width":"height"));
        if(isWidthBigger){
            DecimalFormat df=new DecimalFormat("0.00");
            System.out.println((float)150/width);
            String formate = df.format((float)150/width);
            System.out.println(formate);
            double wRatio = (Double)df.parse(formate);
            System.out.println("Width ratio -> "+wRatio);
            nWidth = ((Double)(width*wRatio)).intValue();
        }
        System.out.println("new width -> "+nWidth);
        System.out.println("new height -> ");

    }
}
