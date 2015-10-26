package com.acme.controller;

import com.acme.gen.domain.*;
import com.acme.gen.mapper.*;
//import com.acme.model.mapper.CustomMapper;
import com.acme.model.domain.ItemCategoryLink;
import com.acme.model.mapper.CustomMapper;
import com.acme.util.Constants;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@RestController
@RequestMapping(value = "/item")
public class ItemController{

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    CompanyMapper companyMapper;

    @Autowired
    ContentMapper contentMapper;

    @Autowired
    ItemContentMapper itemContentMapper;

    @Autowired
    CategoryItemMapper categoryItemMapper;

    @Autowired
    CustomMapper customMapper;

    @Autowired
    CategoryMapper categoryMapper;

    /**
     * Get all items
     **/
    @RequestMapping(method = RequestMethod.GET)
    public JSONArray getItems() throws JsonProcessingException, ParseException {
        List<ItemCategoryLink> links = customMapper.getItemCategories();
//        System.out.println(links);
        if(links!=null && links.size()>0){
            JSONParser parser = new JSONParser();
            ObjectMapper mapper = new ObjectMapper();
            JSONArray array = (JSONArray) parser.parse(mapper.writeValueAsString(links));
            int arrSize = array.size();
            JSONObject object;
            for(int i=0;i<arrSize;i++){
                object = (JSONObject) array.get(i);
                //can return NULL
                Integer val = customMapper.getOrderedItemCou((String) object.get("id"));
                object.put("inOrder", val!=null? val :0);
                array.set(i, object);
            }
            return array;
        } else {
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public ItemCategoryLink getGoodById(@PathVariable("id") String id) {
        Item item = itemMapper.selectByPrimaryKey(id);
        ItemCategoryLink link = new ItemCategoryLink(item);

        CategoryItemExample categoryItemExample = new CategoryItemExample();
        categoryItemExample.createCriteria().andItemIdEqualTo(item.getId());

        CategoryExample categoryExample = new CategoryExample();
        categoryExample.createCriteria().andIdIn(Lists.transform(categoryItemMapper.selectByExample(categoryItemExample), new Function<CategoryItem, String>() {
            @Nullable
            @Override
            public String apply(CategoryItem categoryItem) {
                return categoryItem.getCategoryId();
            }
        }));
        link.setCategories(categoryMapper.selectByExample(categoryExample));
        return link;
    }

    @RequestMapping(method = RequestMethod.GET,value = "/order/{id}")
    public List<Item> getGoodsByOrderId(@PathVariable("id") String orderId) {
        ItemExample itemExample = new ItemExample();
        OrderItemExample orderItemsExample = new OrderItemExample();
        orderItemsExample.createCriteria().andOrderIdEqualTo(orderId);
        List<String> items = Lists.transform(orderItemMapper.selectByExample(orderItemsExample), new Function<OrderItem, String>() {
            @Override
            public String apply(OrderItem orderItem) {
                return orderItem.getItemId();
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

    /**
     * Get all items as map
     **/
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
    public ItemCategoryLink addGood(@RequestBody String input) throws ParseException, IOException {

        ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        JSONParser parser=new JSONParser();
        JSONObject main = (JSONObject) parser.parse(input);

        String itemStr = main.toString();
        System.out.println(itemStr);

        Item item = mapper.readValue(itemStr,Item.class);
        System.out.println(item);

        JSONArray array = (JSONArray)main.get("categories");
        List<String> list = Lists.newArrayList();
        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonobject = (JSONObject)array.get(i);
            list.add(jsonobject.get("id").toString());
        }
        System.out.println(list);

        /* update item object */
        if(item.getId()!=null){
            itemMapper.updateByPrimaryKeySelective(item);
        } else {
            itemMapper.insertSelective(item);
        }

        /* update or create link with categories */

        /* collect and remove old one */
        CategoryItemExample excludeExample = new CategoryItemExample();
        excludeExample.createCriteria().andItemIdEqualTo(item.getId());
        categoryItemMapper.deleteByExample(excludeExample);
        /* add new */
        CategoryItem categoryItem;
        for(String categoryId : list){
            categoryItem = new CategoryItem();
            categoryItem.setItemId(item.getId());
            categoryItem.setCategoryId(categoryId);
            categoryItemMapper.insertSelective(categoryItem);
        }
        /* return linked item */
        ItemCategoryLink link = new ItemCategoryLink(item);
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.createCriteria().andIdIn(list);
        link.setCategories(categoryMapper.selectByExample(categoryExample));

        return link;
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public boolean deleteGood(@PathVariable("id") String id) {
        //delete item in orders
        OrderItemExample orderItemsExample = new OrderItemExample();
        orderItemsExample.createCriteria().andItemIdEqualTo(id);
        orderItemMapper.deleteByExample(orderItemsExample);
        //delete item itself
        itemMapper.deleteByPrimaryKey(id);
        return true;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/filter")
    public List<ItemCategoryLink> filter(@RequestBody String input) throws ParseException, IOException {
//        System.out.println(input);
        JSONParser parser=new JSONParser();
        JSONObject main = (JSONObject) parser.parse(input);
//        System.out.println(main);

        String name = Strings.emptyToNull(main.get("name").toString().toLowerCase());
        System.out.println(name);
        String company = Strings.emptyToNull(main.get("company").toString());
        System.out.println(company);
        String article = Strings.emptyToNull(main.get("article").toString());
        System.out.println(article);

        return customMapper.getFilteredItems(name,article,company);
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

        ItemExample itemExample = new ItemExample();
        itemExample.createCriteria().andNotForSaleEqualTo(false);

        List<Item> items = itemMapper.selectByExample(itemExample);
        for(Item item : items){
            jsonObject = new JSONObject();

            //check orig image
            itemContentExample = new ItemContentExample();
            itemContentExample.createCriteria().andItemIdEqualTo(item.getId()).andShowEqualTo(true);
            List<ItemContent> itemContents = itemContentMapper.selectByExample(itemContentExample);
            if(itemContents.size()>0){
                //take main image
                Iterator<ItemContent> iterator = itemContents.iterator();
                while(iterator.hasNext()){
                    ItemContent itemContent = iterator.next();
                    if(itemContent.isMain()){
                        String contentId = itemContent.getContentId();
                        jsonObject.put("url", Constants.ORIG_URL+contentId);
                        break;
                    }
                }
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

//        String noImage = Constants.VIEW_URL+content.getId();

        Item item = itemMapper.selectByPrimaryKey(itemId);

        jsonObject = new JSONObject();

        //check orig image
        itemContentExample = new ItemContentExample();
        itemContentExample.createCriteria().andItemIdEqualTo(itemId).andShowEqualTo(true);
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
        jsonObject.put("description", item.getDescription());
        jsonObject.put("price", item.getPrice());
        jsonObject.put("name",item.getName());
        jsonObject.put("id", item.getId());
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
//        itemExample.createCriteria().andCategoryIdIn(customMapper.getSubCategoryLeafs(categoryId));

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
//        itemExample.createCriteria().andTypeIdIn(types).andNotForSaleEqualTo(false);
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

    @RequestMapping(method = RequestMethod.GET,value = "/filter/company")
    public JSONArray filterByCompany(@RequestParam(value = "companyId", required = true) String companyId) throws IOException {
        System.out.println(companyId);
        ItemExample itemExample = new ItemExample();
        itemExample.createCriteria().andCompanyIdEqualTo(companyId).andNotForSaleEqualTo(false);
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

    @RequestMapping(method = RequestMethod.POST,value = "/set/sale")
    public void saleToggle(@RequestBody String input) throws ParseException {
        System.out.println(input);
        JSONParser parser=new JSONParser();
        JSONObject main = (JSONObject) parser.parse(input);

        String itemId = main.get("itemId").toString();
        Boolean notForSale = (Boolean) main.get("notForSale");

        Item item = new Item();
        item.setId(itemId);
        item.setNotForSale(notForSale);

        itemMapper.updateByPrimaryKeySelective(item);
    }


}

