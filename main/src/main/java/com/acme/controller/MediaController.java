package com.acme.controller;

import com.acme.gen.domain.Content;
import com.acme.gen.mapper.ContentMapper;
import com.acme.util.ImageCropper;
import com.acme.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

@RestController
@RequestMapping(value = "/media")
public class MediaController {

    @Autowired
    ContentMapper contentMapper;

    @RequestMapping(method = RequestMethod.GET,value = "/image/preview/{contentId}")
    public void getImageForPreview(@PathVariable(value = "contentId") String contentId, HttpServletResponse response) throws Exception {
        Content content = contentMapper.selectByPrimaryKey(contentId);
        response.setContentType(content.getMime());
        ImageIO.write(ImageCropper.cropForPreview(content.getContent()), content.getType(), response.getOutputStream());
    }

    @RequestMapping(method = RequestMethod.GET,value = "/image/view/{contentId}")
    public void getImageForView(@PathVariable(value = "contentId") String contentId, HttpServletResponse response) throws Exception {
        Content content = contentMapper.selectByPrimaryKey(contentId);
        response.setContentType(content.getMime());
        ImageIO.write(ImageCropper.cropForView(content.getContent()), content.getType(), response.getOutputStream());
    }

    @RequestMapping(method = RequestMethod.GET,value = "/image/thumb/{contentId}")
    public void getImageForThumb(@PathVariable(value = "contentId") String contentId, HttpServletResponse response) throws Exception {
        Content content = contentMapper.selectByPrimaryKey(contentId);
        response.setContentType(content.getMime());
        ImageIO.write(ImageCropper.cropForThumb(content.getContent()), content.getType(), response.getOutputStream());
    }

    @RequestMapping(method = RequestMethod.GET,value = "/image/gallery/{contentId}")
    public void getImageForGallery(@PathVariable(value = "contentId") String contentId, HttpServletResponse response) throws Exception {
        Content content = contentMapper.selectByPrimaryKey(contentId);
        response.setContentType(content.getMime());
        ImageIO.write(ImageCropper.cropForGallery(content.getContent()), content.getType(), response.getOutputStream());
    }

    @RequestMapping(method = RequestMethod.GET,value = "/image/{contentId}")
    public void getImageForOrig(@PathVariable(value = "contentId") String contentId, HttpServletResponse response) throws Exception {
        Content content = contentMapper.selectByPrimaryKey(contentId);
        response.setContentType(content.getMime());
//        ImageIO.write(ImageUtils.getImage(content.getContent()), content.getType(),response.getOutputStream());

        float quality = 0.5f;

        // get all image writers for JPG format
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(content.getType());

        if (!writers.hasNext())
            throw new IllegalStateException("No writers found");

        ImageWriter writer = writers.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(response.getOutputStream());
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();

        // compress to a given quality
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        // appends a complete image stream containing a single image and
        //associated stream and image metadata and thumbnails to the output
        writer.write(null, new IIOImage(ImageUtils.getImage(content.getContent()), null, null), param);
        writer.dispose();
    }

}
