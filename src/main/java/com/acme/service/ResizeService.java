package com.acme.service;

import java.awt.image.BufferedImage;

public interface ResizeService {

    BufferedImage forView(byte[] data) throws Exception;

    BufferedImage forGallery(byte[] data) throws Exception;

    BufferedImage forPreview(byte[] data) throws Exception;

    BufferedImage forThumb(byte[] data) throws Exception;

    BufferedImage forOrign(byte[] data) throws Exception;

}
