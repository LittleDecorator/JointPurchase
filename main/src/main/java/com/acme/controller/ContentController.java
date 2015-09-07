package com.acme.controller;

import com.acme.gen.domain.Content;
import com.acme.gen.domain.ItemContent;
import com.acme.gen.domain.ItemContentExample;
import com.acme.gen.mapper.ContentMapper;
import com.acme.gen.mapper.ItemContentMapper;
import com.acme.util.Constants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
        System.out.println("came TO controller");
        //TODO: MAYBE NEED HASH CHECK
        JSONArray array = new JSONArray();
        JSONObject object;

        Content content;
        ItemContent itemContent;
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
                itemContentMapper.insertSelective(itemContent);

                //for return
                object = new JSONObject();
                object.put("url", Constants.PREVIEW_URL + content.getId());
                object.put("id",content.getId());
                object.put("isMain",itemContent.isMain());
                object.put("isShown",itemContent.isShow());

                array.add(object);
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

        for(ItemContent item : itemContentMapper.selectByExample(itemContentExample)){
            jsonObject = new JSONObject();
            jsonObject.put("url", Constants.GALLERY_URL+item.getContentId());
//            jsonObject.put("url", Constants.ORIG_URL+item.getContentId());
            jsonObject.put("id", item.getContentId());
            jsonObject.put("isMain", item.isMain());
            jsonObject.put("isShown", item.isShow());
            array.add(jsonObject);
        }
        return array;
    }

    @RequestMapping(value = "/remove",method = RequestMethod.DELETE)
    public void contentDelete(@RequestParam(value = "id", required = true) String itemImageId){
        String contentId = itemContentMapper.selectByPrimaryKey(itemImageId).getContentId();
        //delete link between content and item
        itemContentMapper.deleteByPrimaryKey(itemImageId);
        //delete content
        contentMapper.deleteByPrimaryKey(contentId);
    }

    @RequestMapping(value = "/set/main",method = RequestMethod.PUT)
    public void setAsMain(@RequestParam(value = "id", required = true) String itemImageId){
        ItemContent itemContent = itemContentMapper.selectByPrimaryKey(itemImageId);
        itemContent.setMain(true);
        itemContentMapper.updateByPrimaryKey(itemContent);
    }


//TODO check then delete
    /*@RequestMapping(value = "/image/{contentId}", method = RequestMethod.GET)
    public JSONObject itemImageUpload(@PathVariable(value = "contentId") String contentId) throws Exception {
        Content content = contentMapper.selectByPrimaryKey(contentId);
        JSONObject object = new JSONObject();
        String type = content.getMime();
        object.put("name",content.getFileName());
        object.put("content", ImageUtils.encodeToString(ImageCropper.resizeImage(ImageUtils.getImage(content.getContent()), false), type.substring(type.indexOf("/")+1)));

        return object;
    }*/
//TODO delete below
/*    @RequestMapping(value = "/image/{contentId}", method = RequestMethod.GET)
    public void getFile(@PathVariable(value = "contentId") String contentId, HttpServletResponse response) throws IOException {
        Content content = contentMapper.selectByPrimaryKey(contentId);
        String type = content.getMime();
        type = type.substring(type.indexOf("/")+1);
        response.setContentType(content.getMime());
        ImageUtils.writeToStream(content.getContent(),type,response.getOutputStream());
    }*/
}
