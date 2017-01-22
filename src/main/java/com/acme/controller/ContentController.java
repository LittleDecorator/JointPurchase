package com.acme.controller;

import com.acme.model.Content;
import com.acme.model.ItemContent;
import com.acme.repository.ContentRepository;
import com.acme.repository.ItemContentRepository;
import com.acme.constant.Constants;
import com.google.common.collect.Maps;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Map;

@RestController
@RequestMapping(value = "/content")
public class ContentController{

    @Autowired
    ItemContentRepository itemContentRepository;

    @Autowired
    ContentRepository contentRepository;

    @RequestMapping(value = "/upload/crop", method = RequestMethod.POST)
    public void itemImageUpload(MultipartHttpServletRequest request ,
                                     @RequestParam(value = "itemId") String itemId,
                                     @RequestParam(value = "imageId") String imageId) throws Exception {
        //TODO: MAYBE NEED HASH CHECK
        Content content;
        ItemContent itemContent;

        Map<String, MultipartFile> fileMap = request.getFileMap();

        for(MultipartFile file : fileMap.values()) {
            if (!file.isEmpty()) {

                String fileName = file.getOriginalFilename();
                String type = fileName.substring(fileName.indexOf(".") + 1);

                content = new Content();
                content.setFileName(file.getOriginalFilename());
                content.setContent(file.getBytes());
                content.setType(type);
                content.setMime("image/" + type);
                contentRepository.insert(content);

                itemContent = itemContentRepository.getByItemIdAndImageId(imageId, itemId);
                itemContent.setCropId(content.getId());
                itemContentRepository.updateSelectiveById(itemContent);
            }
        }
    }

    @RequestMapping(value = "/upload/item", method = RequestMethod.POST)
    public JSONArray itemImageUpload(MultipartHttpServletRequest request, @RequestParam(value = "itemId") String itemId) throws Exception {
        //TODO: MAYBE NEED HASH CHECK
        JSONArray array = new JSONArray();
        JSONObject object;

        Content content;
        ItemContent itemContent;

        //check if any image for item present
        int i = itemContentRepository.countByItemId(itemId);

        Map<String, MultipartFile> fileMap = request.getFileMap();

        for(MultipartFile file : fileMap.values()){
            if (!file.isEmpty()) {

                String fileName = file.getOriginalFilename();
                String type = fileName.substring(fileName.indexOf(".")+1);

                content = new Content();
                content.setFileName(file.getOriginalFilename());
                content.setContent(file.getBytes());
                content.setType(type);
                content.setMime("image/" + type);
                contentRepository.insert(content);

                itemContent = new ItemContent();
                itemContent.setItemId(itemId);
                itemContent.setContentId(content.getId());
                itemContent.setShow(true);
                if(i==0){
                    itemContent.setMain(true);
                } else {
                    itemContent.setMain(false);
                }
                itemContentRepository.insert(itemContent);

                //for return
                object = new JSONObject();
                object.put("url", Constants.PREVIEW_URL + content.getId());
                object.put("id",content.getId());
                object.put("main",itemContent.isMain());
                object.put("show",itemContent.isShow());

                array.add(object);
                i++;
            }
        }
        return array;
    }

    @RequestMapping(value = "/items", method = RequestMethod.GET)
    public JSONArray getPreviewImages(@RequestParam(value = "itemId") String itemId) throws Exception {
        JSONArray array = new JSONArray();
        JSONObject jsonObject;

        for(ItemContent item : itemContentRepository.getByItemId(itemId)){
            jsonObject = new JSONObject();
            jsonObject.put("url", Constants.GALLERY_URL+ (item.getCropId()== null ? item.getContentId() : item.getCropId()));
            jsonObject.put("id", item.getContentId());
            jsonObject.put("main", item.isMain());
            jsonObject.put("show", item.isShow());
            array.add(jsonObject);
        }
        return array;
    }

    @RequestMapping(value = "/remove",method = RequestMethod.DELETE)
    public void contentDelete(@RequestParam(value = "contentId", required = true) String contentId,
                              @RequestParam(value = "itemId", required = true) String itemId){
        itemContentRepository.deleteByContentIdAndItemId(contentId,itemId);
        //delete content
        contentRepository.deleteByID(contentId);
    }

    @RequestMapping(value = "/set/main",method = RequestMethod.PUT)
    public void setAsMain(@RequestBody String input) throws ParseException {

        JSONParser parser=new JSONParser();
        JSONObject object = (JSONObject) parser.parse(input);

        String contentId = object.get("contentId").toString();
        String itemId = object.get("itemId").toString();
        Boolean main = (Boolean) object.get("main");
        Boolean show = (Boolean) object.get("show");

        //set all unmain
        ItemContent itemContent = new ItemContent();
        itemContent.setMain(false);
        Map<String,Object> where = Maps.newHashMap();
        where.put("item_id", itemId);
        itemContentRepository.updateSelective(itemContent, where);

        //set specific main
        itemContent.setShow(show);
        itemContent.setMain(main);
        where = Maps.newHashMap();
        where.put("content_id",contentId);
        where.put("item_id", itemId);
        itemContentRepository.updateSelective(itemContent, where);
    }

    @RequestMapping(value = "/set/show",method = RequestMethod.PUT)
    public void setAsShown(@RequestBody String input) throws ParseException {
        JSONParser parser=new JSONParser();
        JSONObject object = (JSONObject) parser.parse(input);

        String contentId = object.get("contentId").toString();
        String itemId = object.get("itemId").toString();
        Boolean show = (Boolean) object.get("show");

        ItemContent itemContent = new ItemContent();
        itemContent.setShow(show);

        Map<String,Object> where = Maps.newHashMap();
        where.put("content_id",contentId);
        where.put("item_id", itemId);
        itemContentRepository.updateSelective(itemContent, where);
    }
}
