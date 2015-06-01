package com.acme.util;

import com.itextpdf.text.pdf.codec.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {

    private static final String BASE = "data:image/jpeg;base64,";

    public static BufferedImage decodeToImage(String imageString) {

        BufferedImage image = null;
        byte[] imageByte;
        try {
            imageByte = Base64.decode(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public static BufferedImage getImage(byte[] binary) throws IOException {
        System.out.println(binary.length);
        BufferedImage image = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(binary);
        image = ImageIO.read(bis);
        bis.close();
        return image;
    }

    public static String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            imageString = BASE + Base64.encodeBytes(imageBytes);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }



    /*public static void main (String args[]) throws IOException {
        *//* Test image to string and string to image start *//*
        BufferedImage img = ImageIO.read(new File("files/img/TestImage.png"));
        BufferedImage newImg;
        String imgstr;
        imgstr = encodeToString(img, "png");
        System.out.println(imgstr);
        newImg = decodeToImage(imgstr);
        ImageIO.write(newImg, "png", new File("files/img/CopyOfTestImage.png"));
        *//* Test image to string and string to image finish *//*
    }*/

}
