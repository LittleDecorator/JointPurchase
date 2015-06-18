import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

public class Resize {

    private static final int PREVIEW_SIDE = 200;
    private static final int VIEW_SIDE = 300;

    public static void main(String[] args) throws ParseException {

        int origin_w = 652;
        System.out.println("Original width -> "+origin_w);
        int origin_h = 600;
        System.out.println("Original height -> "+origin_h);
        Number origionRation;
        int resize_w,resize_h;
        boolean asPreview = false;

        DecimalFormat df = new DecimalFormat("#.###");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(new Locale("en", "US")));

        //get side ratio for resize
        if(origin_w > origin_h){
            origionRation = (float) origin_h / origin_w;
            System.out.println("Origin ratio -> "+origionRation);
            String fd = df.format(origionRation.doubleValue()+0.001);
            if(asPreview && (origin_w > PREVIEW_SIDE)){
                resize_w = PREVIEW_SIDE;
                resize_h = ((Double)(PREVIEW_SIDE * (Double.valueOf(fd)))).intValue();
            } else if(!asPreview && (origin_w > VIEW_SIDE)){
                resize_w = VIEW_SIDE;
                resize_h = ((Double)(VIEW_SIDE * (Double.valueOf(fd)))).intValue();
            } else {
                resize_h = origin_h;
                resize_w = origin_w;
            }
        } else {
            origionRation = (float) origin_w / origin_h;
            String fd = df.format(origionRation.doubleValue()+0.001);
            System.out.println(fd);
            if(asPreview && (origin_h > PREVIEW_SIDE)){
                resize_h = PREVIEW_SIDE;
                resize_w = ((Double)(PREVIEW_SIDE * (Double.valueOf(fd)))).intValue();
            } else if(!asPreview && (origin_h > VIEW_SIDE)){
                resize_h = VIEW_SIDE;
                resize_w = ((Double)(VIEW_SIDE * (Double.valueOf(fd)))).intValue();
            } else {
                resize_h = origin_h;
                resize_w = origin_w;
            }
        }
        System.out.println("Resized height-> "+resize_h);
        System.out.println("Resized width -> "+resize_w);

    }
}
