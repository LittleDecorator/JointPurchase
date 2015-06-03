package com.acme.util;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageUtils {

    private static final String BASE = "data:image/jpeg;base64,";

    public static BufferedImage decodeToImage(String imageString) throws IOException {
        byte[] imageByte = Base64.decode(imageString);
        return ImageIO.read(new ByteArrayInputStream(imageByte));
    }

    public static BufferedImage getImage(byte[] binary) throws IOException {
        System.out.println(binary.length);
        ByteArrayInputStream bis = new ByteArrayInputStream(binary);
        BufferedImage image = ImageIO.read(bis);
        bis.close();
        return image;
    }

    public static BufferedImage getImage(String path) throws IOException {
        byte[] binary = Files.readAllBytes(Paths.get(path));
        return getImage(binary);
    }

    public static BufferedImage writeToStream (byte[] binary, String type, OutputStream stream) throws IOException {
        System.out.println(binary.length);
        ByteArrayInputStream bis = new ByteArrayInputStream(binary);
        BufferedImage image = ImageIO.read(bis);
        bis.close();
        ImageIO.write(image, type, stream);
        stream.close();
        return getImage(binary);
    }

    public static BufferedImage writeToStream (String path, OutputStream stream) throws IOException {
        byte[] binary = Files.readAllBytes(Paths.get(path));
        String type = path.substring(path.lastIndexOf(".")+1);
        return writeToStream(binary,type,stream);
    }

    public static String encodeToString(BufferedImage image, String type) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
        ImageIO.write(image, type, bos);
        byte[] imageBytes = bos.toByteArray();
        String imageString = BASE + Base64.encode(imageBytes);
        bos.close();
        return imageString;
    }

        /*public static void main(String[] args) throws IOException{
            ByteArrayOutputStream baos=new ByteArrayOutputStream(1000);
            byte[] binary = Files.readAllBytes(Paths.get("/tmp/no_image_available.png"));
            ByteArrayInputStream bis = new ByteArrayInputStream(binary);
            BufferedImage img=ImageIO.read(bis);
            ImageIO.write(img, "png", baos);
            baos.flush();

            String base64String=Base64.encode(baos.toByteArray());
            baos.close();

            byte[] bytearray = Base64.decode(base64String);

            BufferedImage imag=ImageIO.read(new ByteArrayInputStream(bytearray));
            ImageIO.write(imag, "png", new File("/tmp/CopyOfTestImage.png"));
        }*/
}
