package com.acme.service.impl;

import com.acme.service.ImageService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * Сервис предоставляющий api для чтения и записи изображения
 */
@Service
public class ImageServiceImpl implements ImageService {

    private static final String BASE = "data:image/jpeg;base64,";

    public byte[] downloadImage(String url, String type) throws IOException {
        URL imageURL = new URL(url);
        BufferedImage originalImage = ImageIO.read(imageURL);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(originalImage, type, baos);
        return baos.toByteArray();
    }

    //@Cacheable(value = "decode")
    public BufferedImage decodeToImage(String imageString) throws IOException {
        byte[] imageByte = Base64.decodeBase64(imageString);
        return ImageIO.read(new ByteArrayInputStream(imageByte));
    }

    //@Cacheable(value = "image")
    public BufferedImage getImage(byte[] binary) throws IOException {
        //TODO: переписать с учетом https://github.com/haraldk/TwelveMonkeys/issues/281
        ByteArrayInputStream bis = new ByteArrayInputStream(binary);
        BufferedImage image = ImageIO.read(bis);
        bis.close();
        return image;
    }

    //@Cacheable(value = "image")
    public BufferedImage getImage(String path) throws IOException {
        byte[] binary = Files.readAllBytes(Paths.get(path));
        return getImage(binary);
    }

    //@Cacheable(value = "write")
    public BufferedImage writeToStream (byte[] binary, String type, OutputStream stream) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(binary);
        BufferedImage image = ImageIO.read(bis);
        bis.close();
        ImageIO.write(image, type, stream);
        stream.close();
        return getImage(binary);
    }

    //@Cacheable(value = "write")
    public BufferedImage writeToStream (String path, OutputStream stream) throws IOException {
        byte[] binary = Files.readAllBytes(Paths.get(path));
        String type = path.substring(path.lastIndexOf(".")+1);
        return writeToStream(binary,type,stream);
    }

    //@Cacheable(value = "write")
    public void writeToStream (BufferedImage image, String type, OutputStream stream) throws IOException {
        ImageIO.write(image, type, stream);
        stream.close();
    }

    @Cacheable(value = "encode")
    public String encodeToString(BufferedImage image, String type) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
        ImageIO.write(image, type, bos);
        byte[] imageBytes = bos.toByteArray();
        String imageString = BASE + Base64.encodeBase64String(imageBytes);
        bos.close();
        return imageString;
    }

    //@Cacheable(value = "read")
    public byte[] toBytes(BufferedImage image, String type) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
        ImageIO.write(image, type, bos);
        return bos.toByteArray();
    }

    public ImageWriter getWriter(String type) {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(type);
        if (!writers.hasNext())
            throw new IllegalStateException("No writers found");
        return writers.next();
    }

    public ImageWriteParam getDefaultWriterParam(ImageWriter writer) {
//        float quality = 0.5f;
        float quality = .8f;
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
        }
        return param;
    }
}
