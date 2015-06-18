package com.acme.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ImageCropper {

    private static final int PREVIEW_SIDE = 200;
    private static final int VIEW_SIDE = 300;
    private static final int THUMB_SIDE = 65;

    public static String cropImage(String encodedImage,String type) throws Exception {
        return ImageUtils.encodeToString(ImageUtils.decodeToImage(encodedImage), type);
    }

    public static String cropForView(byte[] data,String type) throws Exception {
        return ImageUtils.encodeToString(resizeImage(ImageUtils.getImage(data), true), type);
    }

    public static BufferedImage cropForView(byte[] data) throws Exception {
        return resizeImage(ImageUtils.getImage(data), true);
    }

    public static String cropForPreview(byte[] data,String type) throws Exception {
        return ImageUtils.encodeToString(resizeImage(ImageUtils.getImage(data), true), type);
    }

    public static BufferedImage cropForPreview(byte[] data) throws Exception {
        return resizeImage(ImageUtils.getImage(data), true);
    }

    public static String cropForThumb(byte[] data,String type) throws Exception {
        return ImageUtils.encodeToString(resizeImage(ImageUtils.getImage(data), true), type);
    }

    public static BufferedImage cropForThumb(byte[] data) throws Exception {
        return resizeImage(ImageUtils.getImage(data), true);
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, boolean asPreview){
        BufferedImage resizedImage = getResizeBuffer(originalImage,asPreview);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);
        g.dispose();
        return resizedImage;
    }

    private static BufferedImage resizeImageWithHint(BufferedImage originalImage, boolean asPreview){

        BufferedImage resizedImage = getResizeBuffer(originalImage,asPreview);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        return resizedImage;
    }

    private static BufferedImage getResizeBuffer(BufferedImage originalImage, boolean asPreview){
        int origin_w = originalImage.getWidth();
        System.out.println("Original width -> "+origin_w);
        int origin_h = originalImage.getHeight();
        System.out.println("Original height -> "+origin_h);
        int resize_w,resize_h;
        Number origionRation;
        BufferedImage image;

        if((origin_w > PREVIEW_SIDE) && (origin_h > PREVIEW_SIDE)){

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
            image = new BufferedImage(resize_w, resize_h, originalImage.getType());
        } else {
            image = originalImage;
        }

        return image;
    }

}
