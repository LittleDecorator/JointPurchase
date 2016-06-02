package com.acme.service;

import java.awt.image.BufferedImage;

public interface CropperService {

    BufferedImage cropForView(byte[] data) throws Exception;

    BufferedImage cropForGallery(byte[] data) throws Exception;

    BufferedImage cropForPreview(byte[] data) throws Exception;

    BufferedImage cropForThumb(byte[] data) throws Exception;

}
