package com.acme.service.impl;

import com.acme.service.ImageService;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

@Service
public class ImageServcieImpl implements ImageService {

    private static final String BASE = "data:image/jpeg;base64,";

    @Cacheable(value = "decode")
    public BufferedImage decodeToImage(String imageString) throws IOException {
        byte[] imageByte = Base64.decode(imageString);
        return ImageIO.read(new ByteArrayInputStream(imageByte));
    }

    @Cacheable(value = "image")
    public BufferedImage getImage(byte[] binary) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(binary);
        BufferedImage image = ImageIO.read(bis);
        bis.close();
        return image;
    }

    @Cacheable(value = "image")
    public BufferedImage getImage(String path) throws IOException {
        byte[] binary = Files.readAllBytes(Paths.get(path));
        return getImage(binary);
    }

    @Cacheable(value = "write")
    public BufferedImage writeToStream (byte[] binary, String type, OutputStream stream) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(binary);
        BufferedImage image = ImageIO.read(bis);
        bis.close();
        ImageIO.write(image, type, stream);
        stream.close();
        return getImage(binary);
    }

    @Cacheable(value = "write")
    public BufferedImage writeToStream (String path, OutputStream stream) throws IOException {
        byte[] binary = Files.readAllBytes(Paths.get(path));
        String type = path.substring(path.lastIndexOf(".")+1);
        return writeToStream(binary,type,stream);
    }

    @Cacheable(value = "encode")
    public String encodeToString(BufferedImage image, String type) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
        ImageIO.write(image, type, bos);
        byte[] imageBytes = bos.toByteArray();
        String imageString = BASE + Base64.encode(imageBytes);
        bos.close();
        return imageString;
    }

    public ImageWriter getWriter(String type) {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(type);
        if (!writers.hasNext())
            throw new IllegalStateException("No writers found");
        return writers.next();
    }

    public ImageWriteParam getDefaultWriterParam(ImageWriter writer) {
        float quality = 0.5f;
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
        }
        return param;
    }
}
