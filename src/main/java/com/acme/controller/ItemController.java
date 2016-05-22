package com.acme.controller;

import com.acme.model.filter.ItemFilter;
import com.acme.model.filter.ProductFilter;
import com.acme.model.*;
import com.acme.repository.*;
import com.acme.util.Constants;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/item")
public class ItemController{

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    ItemContentRepository itemContentRepository;

    @Autowired
    CategoryItemRepository categoryItemRepository;

    @Autowired
    ItemCategoryLinkRepository itemCategoryLinkRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CustomRepository customRepository;

    /**
     * Get all items
     **/
    @RequestMapping(method = RequestMethod.GET)
    public List<Item> getItems() throws JsonProcessingException, ParseException {
        return itemRepository.getAll();
//        List<ItemCategoryLink> links = itemCategoryLinkRepository.getGetAll();
//        if(links!=null && links.size()>0){
//            JSONParser parser = new JSONParser();
//            ObjectMapper mapper = new ObjectMapper();
//            JSONArray array = (JSONArray) parser.parse(mapper.writeValueAsString(links));
//            int arrSize = array.size();
//            JSONObject object;
//            for(int i=0;i<arrSize;i++){
//                object = (JSONObject) array.get(i);
//                //can return NULL
//                Integer val = customRepository.getOrderedItemCou((String) object.get("id"));
//                object.put("inOrder", val!=null? val :0);
//                array.set(i, object);
//            }
//            return array;
//        } else {
//            return null;
//        }
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public ItemCategoryLink getGoodById(@PathVariable("id") String id) {
        Item item = itemRepository.getById(id);
        ItemCategoryLink link = new ItemCategoryLink(item);

        List<String> categoryIdList = Lists.transform(categoryItemRepository.getByItemId(item.getId()), CategoryItem::getCategoryId);
        link.setCategories(categoryRepository.getByIdList(categoryIdList));
        return link;
    }

    @RequestMapping(method = RequestMethod.GET,value = "/order/{id}")
    public List<Item> getGoodsByOrderId(@PathVariable("id") String orderId) {
        List<String> itemIdList = Lists.transform(orderItemRepository.getByOrderId(orderId), OrderItem::getItemId);
        return itemRepository.getByIdList(itemIdList);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/company/{id}")
    public List<Item> getGoodsByCompanyId(@PathVariable("id")String companyId) {
        return itemRepository.getByCompanyId(companyId);
    }

    /**
     * Get all items as map
     **/
    @RequestMapping(method = RequestMethod.GET,value = "/map")
    public List<Map<String,String>> getItemMap() {
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map;
        for(Item i : itemRepository.getAll()){
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
            itemRepository.updateSelectiveById(item);
        } else {
            itemRepository.insertSelective(item);
        }

        /* update or create link with categories */

        /* collect and remove old one */
        categoryItemRepository.deleteByItemId(item.getId());
        /* add new */
        CategoryItem categoryItem;
        for(String categoryId : list){
            categoryItem = new CategoryItem();
            categoryItem.setItemId(item.getId());
            categoryItem.setCategoryId(categoryId);
            categoryItemRepository.insertSelective(categoryItem);
        }
        /* return linked item */
        ItemCategoryLink link = new ItemCategoryLink(item);
        link.setCategories(categoryRepository.getByIdList(list));

        return link;
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public boolean deleteGood(@PathVariable("id") String id) {
        //delete item in orders
        orderItemRepository.deleteByItemId(id);
        //delete item itself
        itemRepository.deleteById(id);
        return true;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/filter")
    public List<ItemCategoryLink> filter(@RequestBody(required = false) ItemFilter filter) throws ParseException, IOException {
        List<ItemCategoryLink> result;
        if(filter == null ){
            result = itemCategoryLinkRepository.getGetAll();
        } else {
            result = itemCategoryLinkRepository.getFilteredItems(filter.getName(),filter.getArticle(),filter.getCompanyId());
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.GET,value = "{id}/detail")
    public JSONObject getItemDetail(@PathVariable("id") String itemId) throws Exception {
        JSONObject jsonObject;

        // get base64 default image if no orig image linked with item
        Content content = contentRepository.getDefault().get(0);
        Item item = itemRepository.getById(itemId);

        jsonObject = new JSONObject();

        //check orig image
        List<ItemContent> itemContents = itemContentRepository.getShowedByItemId(itemId);

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
    public List<Product> getCategoriesPreviewItems(@RequestBody ProductFilter filter) throws Exception {
        List<Product> list = customRepository.getItemsByFilter(filter);
        for(Product product : list){
            product.setImageUrl(Constants.PREVIEW_URL+product.getContentId());
        }
        return list;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/filter/type")
    public JSONArray filterByTypes(@RequestBody String input) throws IOException {
        ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        List<String> types = mapper.readValue(input, new TypeReference<List<String>>() {
        });
        List<Item> itemList = itemRepository.getAll();

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;

        // get base64 default image if no orig image linked with item
        Content defContent = contentRepository.getDefault().get(0);

        String noImage = Constants.PREVIEW_URL+defContent.getId();

        for(Item item : itemList){
            jsonObject = new JSONObject();

            //check orig image
            List<ItemContent> itemContents = itemContentRepository.getByItemId(item.getId());
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
        List<Item> itemList = itemRepository.getByCompanyForSale(companyId);

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;

        // get base64 default image if no orig image linked with item
        Content defContent = contentRepository.getDefault().get(0);

        String noImage = Constants.PREVIEW_URL+defContent.getId();

        for(Item item : itemList){
            jsonObject = new JSONObject();

            //check orig image
            List<ItemContent> itemContents = itemContentRepository.getByItemId(item.getId());
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
        itemRepository.updateSelectiveById(item);
    }

    @RequestMapping(method = RequestMethod.POST,value = "search")
    public List<Item> searchItem(@RequestBody String input) throws ParseException {
        System.out.println(input);
        JSONParser parser=new JSONParser();
        JSONObject main = (JSONObject) parser.parse(input);

        String criteria = main.get("criteria").toString();

        List<Item> result = itemRepository.getBySearch(criteria);
        System.out.println(result);
        return result;
    }
}

