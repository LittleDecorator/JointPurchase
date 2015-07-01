package com.acme.controller;

import com.acme.gen.domain.Content;
import com.acme.gen.mapper.ContentMapper;
import com.acme.util.ImageCropper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

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

}
