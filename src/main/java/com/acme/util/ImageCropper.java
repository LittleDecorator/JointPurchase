package com.acme.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.text.DecimalFormat;

public class ImageCropper {

    static Rectangle clip;
    private static final int PREVIEW_SIDE = 200;
    private static final int VIEW_SIDE = 300;
    private static final int THUMB_SIDE = 65;
    private static final int CROP_X = 0;
    private static final int CROP_Y = 0;

    public static String cropImage(String encodedImage,String type) throws Exception {
        return ImageUtils.encodeToString(cropImage(ImageUtils.decodeToImage(encodedImage), PREVIEW_SIDE, PREVIEW_SIDE, 0, 0), type);
    }

    public static String cropForView(byte[] data,String type) throws Exception {
        return ImageUtils.encodeToString(cropImage(resizeImage(ImageUtils.getImage(data), true), VIEW_SIDE, VIEW_SIDE, CROP_X, CROP_Y), type);
    }

    public static BufferedImage cropForView(byte[] data) throws Exception {
        return cropImage(resizeImage(ImageUtils.getImage(data), true), VIEW_SIDE, VIEW_SIDE, CROP_X, CROP_Y);
    }

    public static String cropForPreview(byte[] data,String type) throws Exception {
        return ImageUtils.encodeToString(cropImage(resizeImage(ImageUtils.getImage(data),true), PREVIEW_SIDE, PREVIEW_SIDE, CROP_X, CROP_Y), type);
    }

    public static BufferedImage cropForPreview(byte[] data) throws Exception {
        return cropImage(resizeImage(ImageUtils.getImage(data), true), PREVIEW_SIDE, PREVIEW_SIDE, CROP_X, CROP_Y);
    }

    public static String cropForThumb(byte[] data,String type) throws Exception {
        return ImageUtils.encodeToString(cropImage(resizeImage(ImageUtils.getImage(data),true), THUMB_SIDE, THUMB_SIDE, CROP_X, CROP_Y), type);
    }

    public static BufferedImage cropForThumb(byte[] data) throws Exception {
        return cropImage(resizeImage(ImageUtils.getImage(data), true), THUMB_SIDE, THUMB_SIDE, CROP_X, CROP_Y);
    }

    /*public static String cropImage(byte[] data,String type) throws Exception {
        return ImageUtils.encodeToString(cropImage(ImageUtils.getImage(data), CROP_W, CROP_H, CROP_X, CROP_Y), type);
    }*/

    public static BufferedImage cropImage(BufferedImage img, int cropWidth,int cropHeight, int cropStartX, int cropStartY) throws Exception {
        BufferedImage clipped = null;
        Dimension size = new Dimension(cropWidth, cropHeight);

        createClip(img, size, cropStartX, cropStartY);

        try {
            int w = clip.width;
            int h = clip.height;

            clipped = img.getSubimage(clip.x, clip.y, w, h);

        } catch (RasterFormatException rfe) {
            System.out.println("Raster format error: " + rfe.getMessage());
            return null;
        }
        return clipped;
    }


    private static void createClip(BufferedImage img, Dimension size,int clipX, int clipY) throws Exception {

        boolean isClipAreaAdjusted = false;

        if (clipX < 0) {
            clipX = 0;
            isClipAreaAdjusted = true;
        }

        if (clipY < 0) {
            clipY = 0;
            isClipAreaAdjusted = true;
        }

        if ((size.width + clipX) <= img.getWidth() && (size.height + clipY) <= img.getHeight()) {
            clip = new Rectangle(size);
            clip.x = clipX;
            clip.y = clipY;
        } else {
            if ((size.width + clipX) > img.getWidth())
                size.width = img.getWidth() - clipX;

            if ((size.height + clipY) > img.getHeight())
                size.height = img.getHeight() - clipY;

            clip = new Rectangle(size);
            clip.x = clipX;
            clip.y = clipY;

            isClipAreaAdjusted = true;
        }
        if (isClipAreaAdjusted)
            System.out.println("Crop Area Lied Outside The Image. Adjusted The Clip Rectangle\n");
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

            //get side ratio for resize
            if(origin_w > origin_h){
                origionRation = (float) origin_h / origin_w;
                System.out.println("Origin ratio -> "+origionRation);
                String fd = df.format(origionRation.doubleValue()+0.001);
                if(asPreview && (origin_h > PREVIEW_SIDE)){
                    resize_h = PREVIEW_SIDE;
                    System.out.println("Resized height-> "+resize_h);
                    resize_w = ((Double)(PREVIEW_SIDE / (Double.valueOf(fd)))).intValue();
                    System.out.println("Resized width -> "+resize_w);
                } else if(!asPreview && (origin_h > VIEW_SIDE)){
                    resize_h = VIEW_SIDE;
                    System.out.println("Resized height-> "+resize_h);
                    resize_w = ((Double)(VIEW_SIDE / (Double.valueOf(fd)))).intValue();
                    System.out.println("Resized width -> "+resize_w);
                } else {
                    resize_h = origin_h;
                    resize_w = origin_w;
                }
            } else {
                origionRation = (float) origin_w / origin_h;
                String fd = df.format(origionRation.doubleValue()+0.001);
                if(asPreview && (origin_w > PREVIEW_SIDE)){
                    resize_w = PREVIEW_SIDE;
                    System.out.println("Resized width -> "+resize_w);
                    resize_h = ((Double)(PREVIEW_SIDE / (Double.valueOf(fd)))).intValue();
                    System.out.println("Resized height-> "+resize_h);
                } else if(!asPreview && (origin_w > VIEW_SIDE)){
                    resize_w = VIEW_SIDE;
                    System.out.println("Resized width -> "+resize_w);
                    resize_h = ((Double)(VIEW_SIDE / (Double.valueOf(fd)))).intValue();
                    System.out.println("Resized height-> "+resize_h);
                } else {
                    resize_h = origin_h;
                    resize_w = origin_w;
                }
            }
            image = new BufferedImage(resize_w, resize_h, originalImage.getType());
        } else {
            image = originalImage;
        }

        return image;
    }

}
