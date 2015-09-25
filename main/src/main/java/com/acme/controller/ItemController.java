package com.acme.controller;

import com.acme.gen.domain.*;
import com.acme.gen.mapper.*;
//import model.mapper.CustomMapper;
import com.acme.model.mapper.CustomMapper;
import com.acme.util.Constants;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/item")
public class ItemController{

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    ItemOwnerMapper ownerMapper;

    @Autowired
    OrderItemsMapper orderItemsMapper;

    @Autowired
    CompanyMapper companyMapper;

    @Autowired
    ContentMapper contentMapper;

    @Autowired
    ItemContentMapper itemContentMapper;

    @Autowired
    CustomMapper customMapper;

    @RequestMapping(method = RequestMethod.GET)
    public List<Item> getItems() {
        //get all items
        List<Item> items = itemMapper.selectByExample(new ItemExample());
        //get current user items count

        return items;
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public Item getGoodById(@PathVariable("id") String id) {
        return itemMapper.selectByPrimaryKey(id);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/order/{id}")
    public List<Item> getGoodsByOrderId(@PathVariable("id") String orderId) {
        ItemExample itemExample = new ItemExample();
        OrderItemsExample orderItemsExample = new OrderItemsExample();
        orderItemsExample.createCriteria().andOrderIdEqualTo(orderId);
        List<String> items = Lists.transform(orderItemsMapper.selectByExample(orderItemsExample), new Function<OrderItems, String>() {
            @Override
            public String apply(OrderItems orderItems) {
                return orderItems.getItemId();
            }
        });
        itemExample.createCriteria().andIdIn(items);
        return itemMapper.selectByExample(itemExample);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/company/{id}")
    public List<Item> getGoodsByCompanyId(@PathVariable("id")String companyId) {
        ItemExample itemExample = new ItemExample();
        itemExample.createCriteria().andCompanyIdEqualTo(companyId);
        return itemMapper.selectByExample(itemExample);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/map")
    public List<Map<String,String>> getItemMap() {
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map;
        for(Item i : itemMapper.selectByExample(new ItemExample())){
            map = new HashMap<>();
            map.put("id",i.getId());
            map.put("name",i.getName());
            list.add(map);
        }
        return list;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Item addGood(@RequestBody Item item) {
        if(item.getId()!=null){
            itemMapper.updateByPrimaryKeySelective(item);
        } else {
            itemMapper.insertSelective(item);
        }
        return item;
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public boolean deleteGood(@PathVariable("id") String id) {
        //delete item in orders
        OrderItemsExample orderItemsExample = new OrderItemsExample();
        orderItemsExample.createCriteria().andItemIdEqualTo(id);
        orderItemsMapper.deleteByExample(orderItemsExample);
        //delete item itself
        itemMapper.deleteByPrimaryKey(id);
        return true;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/filter")
    public List<Item> filter(@RequestBody String input) throws ParseException, IOException {
        System.out.println(input);
        JSONParser parser=new JSONParser();
        JSONObject main = (JSONObject) parser.parse(input);
        System.out.println(main);

        ItemExample itemExample = new ItemExample();
        ItemExample.Criteria criteria= itemExample.createCriteria();

        String name = main.get("name").toString();
        String company = main.get("company").toString();
        String category = main.get("category").toString();
        String article = main.get("article").toString();
//
        if(!Strings.isNullOrEmpty(name)){
            criteria.andNameLike("%"+name+"%");
        }
        if(!Strings.isNullOrEmpty(company)){
            criteria.andCompanyIdEqualTo(company);
        }
        if(!Strings.isNullOrEmpty(category)){
            criteria.andTypeIdEqualTo(category);
        }
        if(!Strings.isNullOrEmpty(article)){
            criteria.andArticleLike(article);
        }

        return itemMapper.selectByExample(itemExample);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/preview")
    public JSONArray getPreviewItems() throws Exception {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        ItemContentExample itemContentExample;

        // get base64 default image if no orig image linked with item
        ContentExample example = new ContentExample();
        example.createCriteria().andIsDefaultEqualTo(true);
        Content defContent = contentMapper.selectByExample(example).get(0);

        String noImage = Constants.PREVIEW_URL+defContent.getId();

        List<Item> items = itemMapper.selectByExample(new ItemExample());
        for(Item item : items){
            jsonObject = new JSONObject();

            //check orig image
            itemContentExample = new ItemContentExample();
            itemContentExample.createCriteria().andItemIdEqualTo(item.getId());
            List<ItemContent> itemContents = itemContentMapper.selectByExample(itemContentExample);
            if(itemContents.size()>0){
                //just take first image
                String contentId = itemContents.get(0).getContentId();
//                jsonObject.put("url", Constants.PREVIEW_URL+contentId);
                jsonObject.put("url", Constants.ORIG_URL+contentId);
            } else {
                //else default image
                jsonObject.put("url",noImage);
            }
            jsonObject.put("description",item.getDescription());
            jsonObject.put("price",item.getPrice());
            jsonObject.put("name",item.getName());
            jsonObject.put("id",item.getId());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @RequestMapping(method = RequestMethod.GET,value = "{id}/detail")
    public JSONObject getItemDetail(@PathVariable("id") String itemId) throws Exception {
        JSONObject jsonObject;
        ItemContentExample itemContentExample;

        // get base64 default image if no orig image linked with item
        ContentExample example = new ContentExample();
        example.createCriteria().andIsDefaultEqualTo(true);
        Content content = contentMapper.selectByExample(example).get(0);

        String noImage = Constants.VIEW_URL+content.getId();

        Item item = itemMapper.selectByPrimaryKey(itemId);

        jsonObject = new JSONObject();

        //check orig image
        itemContentExample = new ItemContentExample();
        itemContentExample.createCriteria().andItemIdEqualTo(itemId);
        List<ItemContent> itemContents = itemContentMapper.selectByExample(itemContentExample);

        JSONArray jsonArray = new JSONArray();
        if(itemContents.size()>0){
            //take all image
            for(ItemContent itemContent : itemContents){
                jsonArray.add(itemContent.getContentId());
            }
        } else {
            //else default image
            jsonArray.add(content.getId());
        }
        jsonObject.put("description",item.getDescription());
        jsonObject.put("price", item.getPrice());
        jsonObject.put("name",item.getName());
        jsonObject.put("id",item.getId());
        jsonObject.put("media",jsonArray);
        return jsonObject;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/preview")
    public JSONArray getCategoriesPreviewItems(@RequestBody String input) throws Exception {
        JSONParser parser=new JSONParser();
        String categoryId = ((JSONObject) parser.parse(input)).get("categoryId").toString();
        //TODO: find all categories in tree from specific node

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        ItemContentExample itemContentExample;

        // get base64 default image if no orig image linked with item
        ContentExample example = new ContentExample();
        example.createCriteria().andIsDefaultEqualTo(true);
        Content defContent = contentMapper.selectByExample(example).get(0);

        String noImage = Constants.PREVIEW_URL+defContent.getId();

        ItemExample itemExample = new ItemExample();
        //TODO: use this category list down here
        itemExample.createCriteria().andTypeIdIn(customMapper.getSubCategoryLeafs(categoryId));

        List<Item> items = itemMapper.selectByExample(itemExample);
        for(Item item : items){
            jsonObject = new JSONObject();

            //check orig image
            itemContentExample = new ItemContentExample();
            itemContentExample.createCriteria().andItemIdEqualTo(item.getId());
            List<ItemContent> itemContents = itemContentMapper.selectByExample(itemContentExample);
            if(itemContents.size()>0){
                //just take first image
                String contentId = itemContents.get(0).getContentId();
                jsonObject.put("url", Constants.PREVIEW_URL+contentId);
            } else {
                //else default image
                jsonObject.put("url",noImage);
            }
            jsonObject.put("description",item.getDescription());
            jsonObject.put("price",item.getPrice());
            jsonObject.put("name",item.getName());
            jsonObject.put("id",item.getId());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/filter/type")
    public JSONArray filterByTypes(@RequestBody String input) throws IOException {
        ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        List<String> types = mapper.readValue(input, new TypeReference<List<String>>(){});

        ItemExample itemExample = new ItemExample();
        itemExample.createCriteria().andTypeIdIn(types);
        List<Item> itemList = itemMapper.selectByExample(itemExample);

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        ItemContentExample itemContentExample;

        // get base64 default image if no orig image linked with item
        ContentExample example = new ContentExample();
        example.createCriteria().andIsDefaultEqualTo(true);
        Content defContent = contentMapper.selectByExample(example).get(0);

        String noImage = Constants.PREVIEW_URL+defContent.getId();

        for(Item item : itemList){
            jsonObject = new JSONObject();

            //check orig image
            itemContentExample = new ItemContentExample();
            itemContentExample.createCriteria().andItemIdEqualTo(item.getId());
            List<ItemContent> itemContents = itemContentMapper.selectByExample(itemContentExample);
            if(itemContents.size()>0){
                //just take first image
                String contentId = itemContents.get(0).getContentId();
//                jsonObject.put("url", Constants.PREVIEW_URL+contentId);
                jsonObject.put("url", Constants.ORIG_URL+contentId);
            } else {
                //else default image
                jsonObject.put("url",noImage);
            }
            jsonObject.put("description",item.getDescription());
            jsonObject.put("price",item.getPrice());
            jsonObject.put("name",item.getName());
            jsonObject.put("id",item.getId());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    class ItemView implements Serializable{
        Item item;
        int totalInStock;
        int myCount;
        int inOrders;

        public ItemView(Item item, int totalInStock, int myCount, int inOrders) {
            this.item = item;
            this.totalInStock = totalInStock;
            this.myCount = myCount;
            this.inOrders = inOrders;
        }

        public void setItem(Item item) {
            this.item = item;
        }

        public void setTotalInStock(int totalInStock) {
            this.totalInStock = totalInStock;
        }

        public void setMyCount(int myCount) {
            this.myCount = myCount;
        }

        public void setInOrders(int inOrders) {
            this.inOrders = inOrders;
        }
    }
}

