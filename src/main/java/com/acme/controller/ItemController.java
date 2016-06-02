package com.acme.controller;

import com.acme.model.*;
import com.acme.model.dto.ItemMediaTransfer;
import com.acme.model.dto.ItemSearchResult;
import com.acme.model.dto.ItemTransfer;
import com.acme.model.dto.ItemUrlTransfer;
import com.acme.model.filter.ItemFilter;
import com.acme.model.filter.ProductFilter;
import com.acme.repository.*;
import com.acme.service.CategoryService;
import com.acme.service.ItemService;
import com.acme.constant.Constants;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ItemService itemService;

    /**
     * Get all items
     **/
    @RequestMapping(method = RequestMethod.GET)
    public List<Item> getItems(){
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

    @RequestMapping(method = RequestMethod.POST)
    public String addItem(@RequestBody ItemTransfer transfer) throws ParseException, IOException {
        String itemId = null;
        if (transfer != null) {
            System.out.println(transfer);
            TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
            try {
                itemId = UUID.randomUUID().toString();
                transfer.getItem().setId(itemId);
                itemRepository.insertSelective(transfer.getItem());
                /* add new linked categories */
                categoryItemRepository.insertBulk(categoryService.createCategoryItemList4Item(transfer.getItem().getId(), transfer.getCategories()));
                transactionManager.commit(status);
            } catch (Exception ex) {
                ex.printStackTrace();
                transactionManager.rollback(status);
            }
        }
        return itemId;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void updateItem(@RequestBody ItemTransfer transfer){
        System.out.println(transfer);
        if (transfer != null) {
            System.out.println(transfer);
            TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
            try {
                itemRepository.updateSelectiveById(transfer.getItem());
                categoryItemRepository.deleteByItemAndExcludedCategoryIdList(transfer.getItem().getId(), transfer.getCategories());
                transfer.getCategories().removeAll(categoryItemRepository.getByItemId(transfer.getItem().getId()).stream().map(CategoryItem::getCategoryId).collect(Collectors.toList()));
                List<CategoryItem> categoryItems = categoryService.createCategoryItemList4Item(transfer.getItem().getId(),transfer.getCategories());
                categoryItemRepository.insertBulk(categoryItems);
                transactionManager.commit(status);
            } catch (Exception ex) {
                System.out.println(ex);
                transactionManager.rollback(status);
            }
        }
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



    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public void deleteItem(@PathVariable("id") String id) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            orderItemRepository.deleteByItemId(id);
            itemRepository.deleteById(id);
            transactionManager.commit(status);
        } catch (Exception ex){
            System.out.println(ex);
            transactionManager.rollback(status);
        }
    }

    @RequestMapping(method = RequestMethod.POST,value = "/filter")
    public List<ItemCategoryLink> getByFilter(@RequestBody ItemFilter filter) throws ParseException, IOException {
        List<ItemCategoryLink> result;
        if(filter == null ){
            result = itemCategoryLinkRepository.getGetAll();
        } else {
            result = itemCategoryLinkRepository.getFilteredItems(filter.getName(),filter.getArticle(),filter.getCompanyId());
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.GET,value = "{id}/detail")
    public ItemMediaTransfer getItemDetail(@PathVariable("id") String itemId) throws Exception {
        Item item = itemRepository.getById(itemId);
        return itemService.getItemMediaTransfers(item);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/preview")
    public Map<String,Object> getCategoriesPreviewItems(@RequestBody ProductFilter filter) throws Exception {
        List<Category> categoryList = null;
        List<String> categorIds = null;

        if(!Strings.isNullOrEmpty(filter.getCategory())){
            categorIds = customRepository.getCategoriesMapByParentId(filter.getCategory());
            System.out.println(categorIds);
            categoryList = categoryRepository.getByIdList(categorIds);
            System.out.println(categoryList);
        }

        List<Product> list = customRepository.getItemsByFilter(filter,categorIds);

        Content defContent = contentRepository.getDefault();
        for(Product product : list){
            if(Strings.isNullOrEmpty(product.getContentId())){
                product.setContentId(defContent.getId());
                product.setImageUrl(Constants.PREVIEW_URL+defContent.getId());
            } else {
                product.setImageUrl(Constants.PREVIEW_URL+product.getContentId());
            }
        }

        Map<String,Object> result = Maps.newHashMap();
        result.put("items",list);
        result.put("filter",categoryList);
        return result;
    }

    @RequestMapping(method = RequestMethod.GET,value = "/filter/category")
    public List<ItemUrlTransfer> filterByCategory(@RequestParam("categoryId") String categoryId) throws IOException {
        List<Item> itemList = itemRepository.getByCategoryForSale(categoryId);
        return itemService.getItemUrlTransfers(itemList);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/filter/company")
    public List<ItemUrlTransfer> filterByCompany(@RequestParam(value = "companyId", required = true) String companyId) throws IOException {
        List<Item> itemList = itemRepository.getByCompanyForSale(companyId);
        return itemService.getItemUrlTransfers(itemList);
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

    @RequestMapping(method = RequestMethod.GET,value = "search")
    public List<Map<String,Object>> searchItem(@RequestParam(value = "criteria",name = "criteria") String criteria) throws ParseException {
        List<ItemSearchResult> searchResults = itemRepository.getBySearch(criteria);
        Map<String,Category> categories = categoryRepository.getAll().stream().collect(Collectors.toMap(Category::getId, Function.<Category>identity()));
        Content defContent = contentRepository.getDefault();
        List result = Lists.newArrayList();

        Map<String,Object> raw = Maps.newHashMap();
        for(ItemSearchResult searchResult : searchResults){
            Map<String,Object> entry = Maps.newHashMap();
            if(searchResult.getContentId() == null){
                entry.put("content","/media/image/gallery/"+defContent.getId());
            } else {
                entry.put("content","/media/image/gallery/"+searchResult.getContentId());
            }
            entry.put("item",searchResult.getItem());
            if(raw.containsKey(searchResult.getCategoryId())){
                ((List)raw.get(searchResult.getCategoryId())).add(entry);
            } else {
                List rows = Lists.newArrayList(entry);
                raw.put(searchResult.getCategoryId(), rows);
            }
        }

        Map<String,Object> map;
        for(String key : raw.keySet()){
            map = Maps.newHashMap();
            map.put("category",categories.get(key).getName());
            map.put("rows",raw.get(key));
            result.add(map);
        }
        return result;
    }
}

