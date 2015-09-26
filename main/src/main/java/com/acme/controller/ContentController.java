package com.acme.controller;

import com.acme.gen.domain.Content;
import com.acme.gen.domain.ItemContent;
import com.acme.gen.domain.ItemContentExample;
import com.acme.gen.domain.ItemExample;
import com.acme.gen.mapper.ContentMapper;
import com.acme.gen.mapper.ItemContentMapper;
import com.acme.util.Constants;
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

        //check if any image for item present
        ItemContentExample example = new ItemContentExample();
        example.createCriteria().andItemIdEqualTo(itemId);
        int i = itemContentMapper.countByExample(example);

        Map<String, MultipartFile> fileMap = request.getFileMap();

        for(MultipartFile file : fileMap.values()){
            if (!file.isEmpty()) {

                String fileName = file.getOriginalFilename();
                String type = fileName.substring(fileName.indexOf(".")+1);

                content = new Content();
                content.setFileName(file.getOriginalFilename());
                content.setContent(file.getBytes());
                content.setType(type);
                content.setMime("image/"+type);
                contentMapper.insertSelective(content);

                itemContent = new ItemContent();
                itemContent.setItemId(itemId);
                itemContent.setContentId(content.getId());
                itemContent.setShow(true);
                if(i==0){
                    itemContent.setMain(true);
                } else {
                    itemContent.setMain(false);
                }
                itemContentMapper.insertSelective(itemContent);

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
    public JSONArray getPreviewImages(@RequestParam(value = "itemId", required = true) String itemId) throws Exception {
        JSONArray array = new JSONArray();
        JSONObject jsonObject;

        ItemContentExample itemContentExample = new ItemContentExample();
        itemContentExample.createCriteria().andItemIdEqualTo(itemId);
        itemContentExample.setOrderByClause("date_add asc");

        for(ItemContent item : itemContentMapper.selectByExample(itemContentExample)){
            jsonObject = new JSONObject();
            jsonObject.put("url", Constants.GALLERY_URL+item.getContentId());
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
        ItemContentExample example = new ItemContentExample();
        example.createCriteria().andContentIdEqualTo(contentId).andItemIdEqualTo(itemId);
        //delete link between content and item
        itemContentMapper.deleteByExample(example);
        //delete content
        contentMapper.deleteByPrimaryKey(contentId);
    }

    @RequestMapping(value = "/set/main",method = RequestMethod.PUT)
    public void setAsMain(@RequestBody String input) throws ParseException {

        JSONParser parser=new JSONParser();
        JSONObject object = (JSONObject) parser.parse(input);

        String contentId = object.get("contentId").toString();
        String itemId = object.get("itemId").toString();
        Boolean main = (Boolean) object.get("main");
        Boolean show = (Boolean) object.get("show");

        ItemContentExample example = new ItemContentExample();
        example.createCriteria().andItemIdEqualTo(itemId);

        //set all unmain
        ItemContent itemContent = new ItemContent();
        itemContent.setMain(false);
        itemContentMapper.updateByExampleSelective(itemContent, example);

        //set specific main
        itemContent.setShow(show);
        itemContent.setMain(main);
        example = new ItemContentExample();
        example.createCriteria().andContentIdEqualTo(contentId).andItemIdEqualTo(itemId);
        itemContentMapper.updateByExampleSelective(itemContent, example);
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

        ItemContentExample example = new ItemContentExample();
        example.createCriteria().andContentIdEqualTo(contentId).andItemIdEqualTo(itemId);

        itemContentMapper.updateByExampleSelective(itemContent,example);
    }
}
