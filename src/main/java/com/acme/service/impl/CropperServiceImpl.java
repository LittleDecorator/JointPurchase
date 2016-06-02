package com.acme.service.impl;

import com.acme.service.CropperService;
import com.acme.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Service
public class CropperServiceImpl implements CropperService{

    private final int PREVIEW_SIDE = 500;
    private final int GALLERY_SIDE = 500;
    private final int VIEW_SIDE = 500;
    private final int THUMB_SIDE = 100;

    @Autowired
    ImageService imageService;

    @Cacheable(value = "view")
    public BufferedImage cropForView(byte[] data) throws Exception {
        return resizeImage(imageService.getImage(data), VIEW_SIDE);
    }

    @Cacheable(value = "gallery")
    public BufferedImage cropForGallery(byte[] data) throws Exception {
        return resizeImage(imageService.getImage(data), GALLERY_SIDE);
    }

    @Cacheable(value = "preview")
    public BufferedImage cropForPreview(byte[] data) throws Exception {
        return resizeImage(imageService.getImage(data), PREVIEW_SIDE);
    }

    @Cacheable(value = "thumb")
    public BufferedImage cropForThumb(byte[] data) throws Exception {
        return resizeImage(imageService.getImage(data), THUMB_SIDE);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int side){
        BufferedImage resizedImage = getResizeBuffer(originalImage,side);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, resizedImage.getWidth(), resizedImage.getHeight(), null);
        g.dispose();
        return resizedImage;
    }

    private BufferedImage getResizeBuffer(BufferedImage originalImage, int side){
        int origin_w = originalImage.getWidth();
        int origin_h = originalImage.getHeight();
        int resize_w=0,resize_h=0;

        Number origionRation;
        BufferedImage image;

        if((origin_w > side) && (origin_h > side)){

            DecimalFormat df = new DecimalFormat("#.###");
            df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(new Locale("en", "US")));

            //get side ratio for resize
            if(origin_w > origin_h){
                origionRation = (float) origin_h / origin_w;
                String fd = df.format(origionRation.doubleValue()+0.001);
                if((origin_w > side)){
                    resize_w = side;
                    resize_h = ((Double)(side * (Double.valueOf(fd)))).intValue();
                }
            } else {
                origionRation = (float) origin_w / origin_h;
                String fd = df.format(origionRation.doubleValue()+0.001);
                if(origin_h > side){
                    resize_h = side;
                    resize_w = ((Double)(side * (Double.valueOf(fd)))).intValue();
                }
            }
            image = new BufferedImage(resize_w, resize_h, originalImage.getType());
        } else {
            image = originalImage;
        }

        return image;
    }

}
