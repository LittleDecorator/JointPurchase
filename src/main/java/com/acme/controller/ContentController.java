package com.acme.controller;

import com.acme.gen.domain.Content;
import com.acme.gen.domain.ContentExample;
import com.acme.gen.domain.ItemContent;
import com.acme.gen.domain.ItemContentExample;
import com.acme.gen.mapper.ContentMapper;
import com.acme.gen.mapper.ItemContentMapper;
import com.acme.util.ImageCropper;
import com.acme.util.ImageUtils;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/content")
public class ContentController{

    @Autowired
    ItemContentMapper itemContentMapper;

    @Autowired
    ContentMapper contentMapper;

    @RequestMapping(value = "/upload/item", method = RequestMethod.POST)
    public JSONArray itemImageUpload(MultipartHttpServletRequest request ,@RequestParam(value = "itemId", required = true) String itemId) throws Exception {
        //TODO: MAYBE NEED HASH CHECK
        JSONArray array = new JSONArray();
        JSONObject object;

        Content content;
        ItemContent itemContent;
        Map<String, MultipartFile> fileMap = request.getFileMap();
        for(MultipartFile file : fileMap.values()){
            if (!file.isEmpty()) {

                content = new Content();
                content.setFileName(file.getOriginalFilename());
                content.setContent(file.getBytes());
                contentMapper.insertSelective(content);

                itemContent = new ItemContent();
                itemContent.setItemId(itemId);
                itemContent.setContentId(content.getId());
                itemContentMapper.insertSelective(itemContent);

                //for return
                object = new JSONObject();
                object.put("content", ImageCropper.cropForPreview(content.getContent()));
                object.put("id",content.getId());
                array.add(object);
            }
        }
        return array;
    }

    @RequestMapping(value = "/items", method = RequestMethod.GET)
    public JSONArray getPreviewImages(@RequestParam(value = "itemId", required = true) String itemId) throws Exception {
        JSONArray array = null;

        ItemContentExample itemContentExample = new ItemContentExample();
        itemContentExample.createCriteria().andItemIdEqualTo(itemId);

        List<String> contentIdList = Lists.transform(itemContentMapper.selectByExample(itemContentExample), new Function<ItemContent, String>() {
            @Override
            public String apply(ItemContent itemContent) {
                return itemContent.getContentId();
            }
        });

        if(contentIdList.size()>0){
            array = new JSONArray();
            JSONObject object;

            ContentExample contentExample = new ContentExample();
            contentExample.createCriteria().andIdIn(contentIdList);
            for(Content content : contentMapper.selectByExampleWithBLOBs(contentExample)){
                object = new JSONObject();
                object.put("content", ImageUtils.encodeToString(ImageUtils.getImage(content.getContent()), "jpeg"));
                object.put("id", content.getId());
                array.add(object);
            }
        }

        return array;
    }

    @RequestMapping(value = "/image/{contentId}", method = RequestMethod.GET)
    public JSONObject itemImageUpload(@PathVariable(value = "contentId") String contentId) throws Exception {
        Content content = contentMapper.selectByPrimaryKey(contentId);
        JSONObject object = new JSONObject();
        object.put("name",content.getFileName());
        object.put("content", ImageUtils.encodeToString(ImageCropper.resizeImage(ImageUtils.getImage(content.getContent()), false), "jpeg"));
        return object;
    }

}
