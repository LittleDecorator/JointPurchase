package com.acme.controller;

import com.acme.model.Content;
import com.acme.repository.ContentRepository;
import com.acme.service.CropperService;
import com.acme.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

@RestController
@RequestMapping(value = "/media")
public class MediaController {

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    CropperService cropperService;

    @Autowired
    ImageService imageService;

    @RequestMapping(method = RequestMethod.GET,value = "/image/preview/{contentId}")
    public void getImageForPreview(@PathVariable(value = "contentId") String contentId, HttpServletResponse response) throws Exception {
        long start = System.currentTimeMillis();
        Content content = contentRepository.getById(contentId);
        System.out.println("Take " + contentId + " in preview after get content: " + (System.currentTimeMillis() - start) + "ms");
        if("image/webp".contentEquals(content.getMime())) {
            response.getOutputStream().write(content.getContent());
        } else {
            response.setContentType(content.getMime());
            BufferedImage image = cropperService.cropForPreview(content.getContent());
            System.out.println("Take in preview after crop: " + (System.currentTimeMillis() - start) + "ms");
            ImageIO.write(image, content.getType(), response.getOutputStream());
            System.out.println("Take in preview after crop and write: " + (System.currentTimeMillis() - start) + "ms");
        }
    }

    @RequestMapping(method = RequestMethod.GET,value = "/image/view/{contentId}")
    public void getImageForView(@PathVariable(value = "contentId") String contentId, HttpServletResponse response) throws Exception {
        long start = System.currentTimeMillis();
        Content content = contentRepository.getById(contentId);
        System.out.println("Take " + contentId + " in view after get content: " + (System.currentTimeMillis() - start) + "ms");
        if("image/webp".contentEquals(content.getMime())) {
            response.getOutputStream().write(content.getContent());
        } else {
            response.setContentType(content.getMime());
            BufferedImage image = cropperService.cropForView(content.getContent());
            System.out.println("Take in view after crop: " + (System.currentTimeMillis() - start) + "ms");
            ImageIO.write(image, content.getType(), response.getOutputStream());
            System.out.println("Take in view after crop and write: " + (System.currentTimeMillis() - start) + "ms");
        }
    }

    @RequestMapping(method = RequestMethod.GET,value = "/image/thumb/{contentId}")
    public void getImageForThumb(@PathVariable(value = "contentId") String contentId, HttpServletResponse response) throws Exception {
        long start = System.currentTimeMillis();
        Content content = contentRepository.getById(contentId);
        System.out.println("Take " + contentId + " in thumb after get content: " + (System.currentTimeMillis() - start) + "ms");
        if("image/webp".contentEquals(content.getMime())) {
            response.getOutputStream().write(content.getContent());
        } else {
            response.setContentType(content.getMime());
            BufferedImage image = cropperService.cropForThumb(content.getContent());
            System.out.println("Take in thumb after crop: " + (System.currentTimeMillis() - start) + "ms");
            ImageIO.write(image, content.getType(), response.getOutputStream());
            System.out.println("Take in thumb after crop and write: " + (System.currentTimeMillis() - start) + "ms");
        }
    }

    @RequestMapping(method = RequestMethod.GET,value = "/image/gallery/{contentId}")
    public void getImageForGallery(@PathVariable(value = "contentId") String contentId, HttpServletResponse response) throws Exception {
        long start = System.currentTimeMillis();
        Content content = contentRepository.getById(contentId);
        System.out.println("Take " + contentId + " in gallery after get content: " + (System.currentTimeMillis() - start) + "ms");
        if("image/webp".contentEquals(content.getMime())){
            response.getOutputStream().write(content.getContent());
        } else {
            BufferedImage image = cropperService.cropForGallery(content.getContent());
            System.out.println("Take in gallery after crop: " + (System.currentTimeMillis() - start) + "ms");
            response.setContentType(content.getMime());
            ImageIO.write(image, content.getType(), response.getOutputStream());
            System.out.println("Take in gallery after crop and write: " + (System.currentTimeMillis() - start) + "ms");
        }
    }

    @RequestMapping(method = RequestMethod.GET,value = "/image/{contentId}")
    public void getImageForOrig(@PathVariable(value = "contentId") String contentId, HttpServletResponse response) throws Exception {
        long start = System.currentTimeMillis();
        Content content = contentRepository.getById(contentId);
        System.out.println("Take " + contentId + " in raw after get content: " + (System.currentTimeMillis() - start) + "ms");
        if("image/webp".contentEquals(content.getMime())) {
            response.getOutputStream().write(content.getContent());
        } else {
            BufferedImage image = imageService.getImage(content.getContent());
            System.out.println("Take in raw after getImage: " + (System.currentTimeMillis() - start) + "ms");
            response.setContentType(content.getMime());

            // get all image writers for JPG format
            ImageWriter writer = imageService.getWriter(content.getType());
            ImageWriteParam param = imageService.getDefaultWriterParam(writer);
            writer.setOutput(ImageIO.createImageOutputStream(response.getOutputStream()));
            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();
            System.out.println("Take in raw after getImage and write: " + (System.currentTimeMillis() - start) + "ms");
        }
    }

}
