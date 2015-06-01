
import java.text.DecimalFormat;

public class Round {

    public static void main(String[] args) {
        /*DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.FLOOR);
        for (Number n : Arrays.asList(1.51862069,1.572857143)) {
            Double d = n.doubleValue();
            System.out.println(df.format(d));
        }
        df = new DecimalFormat("#.###");
        for (Number n : Arrays.asList(1.51862069,1.572857143)) {
            Double d = n.doubleValue();
            System.out.println(df.format(d));
        }*/

        int origin_w = 1920;
        System.out.println("Original width -> " + origin_w);
        int origin_h = 1200;
        System.out.println("Original height -> " + origin_h);
        int resize_w, resize_h;
        Number origionRation;

        DecimalFormat df = new DecimalFormat("#.###");

        //get side ratio for resize
        if (origin_w > origin_h) {
            origionRation = (float) origin_h / origin_w;
            System.out.println("Origin ratio -> " + origionRation);
            String fd = df.format(origionRation.doubleValue() + 0.001);

            resize_w = ((Double) (700 / (Double.valueOf(fd)))).intValue();
            System.out.println("Resized width -> " + resize_w);
            resize_h = 700;
            System.out.println("Resized height-> " + resize_h);

        }
    }
}
