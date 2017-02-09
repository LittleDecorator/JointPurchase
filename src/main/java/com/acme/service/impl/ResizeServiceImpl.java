package com.acme.service.impl;

import com.acme.enums.ImageSize;
import com.acme.service.ImageService;
import com.acme.service.ResizeService;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Сервис обработки изображений.
 * - оптимизация
 * - ресайз
 */
@Service
public class ResizeServiceImpl implements ResizeService {

    @Autowired
    ImageService imageService;

    @Cacheable(value = "view")
    public BufferedImage forView(byte[] data) throws Exception {
        return resizeImage(imageService.getImage(data), ImageSize.VIEW);
    }

    @Cacheable(value = "gallery")
    public BufferedImage forGallery(byte[] data) throws Exception {
        return resizeImage(imageService.getImage(data), ImageSize.GALLERY);
    }

    @Cacheable(value = "preview")
    public BufferedImage forPreview(byte[] data) throws Exception {
        return resizeImage(imageService.getImage(data), ImageSize.PREVIEW);
    }

    @Cacheable(value = "thumb")
    public BufferedImage forThumb(byte[] data) throws Exception {
        return resizeImage(imageService.getImage(data), ImageSize.THUMB);
    }

    @Cacheable(value = "origin")
    public BufferedImage forOrign(byte[] data) throws Exception {
        return resizeImage(imageService.getImage(data), ImageSize.ORIGINAL);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, ImageSize side) throws IOException {
        return Thumbnails.of(originalImage).width(side.getWidth()).asBufferedImage();
    }

}
