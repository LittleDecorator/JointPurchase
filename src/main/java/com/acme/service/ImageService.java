package com.acme.service;

import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public interface ImageService {

    public byte[] downloadImage(String url, String type) throws IOException;

    BufferedImage decodeToImage(String imageString) throws IOException;

    BufferedImage getImage(byte[] binary) throws IOException;

    BufferedImage getImage(String path) throws IOException;

    BufferedImage writeToStream (byte[] binary, String type, OutputStream stream) throws IOException;

    BufferedImage writeToStream (String path, OutputStream stream) throws IOException;

    void writeToStream (BufferedImage image, String type, OutputStream stream) throws IOException;

    byte[] toBytes(BufferedImage image, String type) throws IOException;

    String encodeToString(BufferedImage image, String type) throws IOException;

    ImageWriter getWriter(String type);

    ImageWriteParam getDefaultWriterParam(ImageWriter writer);
}
