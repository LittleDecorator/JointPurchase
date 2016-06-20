package com.acme.controller;

import com.acme.model.*;
import com.acme.model.dto.ItemView;
import com.acme.model.filter.ItemFilter;
import com.acme.repository.*;
import com.acme.service.CategoryService;
import com.acme.service.ItemService;
import com.google.common.collect.Lists;
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
    PlatformTransactionManager transactionManager;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ItemService itemService;

    /**
     * Get all items
     **/
    @RequestMapping(method = RequestMethod.GET)
    public List<ItemView> getItems(ItemFilter filter){
        return itemRepository.getFilteredItems(filter);
    }

    /**
     * Create new Item
     *
     * @param item
     * @return ID of new item
     * @throws ParseException
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.POST)
    public String addItem(@RequestBody CategorizeItem item) throws ParseException, IOException {
        String itemId = null;
        if (item != null) {
            TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
            try {
                itemId = UUID.randomUUID().toString();
                item.setId(itemId);
                itemRepository.insertSelective(item);
                /* add new linked categories */
                categoryItemRepository.insertBulk(categoryService.createCategoryItemList4Item(itemId, item.getCategories().stream().map(Category::getId).collect(Collectors.toList())));
                transactionManager.commit(status);
            } catch (Exception ex) {
                ex.printStackTrace();
                transactionManager.rollback(status);
            }
        }
        return itemId;
    }

    /**
     * Update item record
     *
     * @param item
     */
    @RequestMapping(method = RequestMethod.PUT)
    public void updateItem(@RequestBody CategorizeItem item){
        if (item != null) {
            TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
            try {
                itemRepository.updateSelectiveById(item);
                List<String> categoryIdList = item.getCategories().stream().map(Category::getId).collect(Collectors.toList());
                categoryItemRepository.deleteByItemAndExcludedCategoryIdList(item.getId(), categoryIdList);
                categoryIdList.removeAll(categoryItemRepository.getByItemId(item.getId()).stream().map(CategoryItem::getCategoryId).collect(Collectors.toList()));
                List<CategoryItem> categoryItems = categoryService.createCategoryItemList4Item(item.getId(),categoryIdList);
                categoryItemRepository.insertBulk(categoryItems);
                transactionManager.commit(status);
            } catch (Exception ex) {
                transactionManager.rollback(status);
            }
        }
    }

    /**
     * Delete Item by id
     * @param id
     */
    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public void deleteItem(@PathVariable("id") String id) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            orderItemRepository.deleteByItemId(id);
            itemContentRepository.deleteByItemId(id);
            categoryItemRepository.deleteByItemId(id);
            itemRepository.deleteById(id);
            transactionManager.commit(status);
        } catch (Exception ex){
            System.out.println(Arrays.toString(ex.getStackTrace()));
            transactionManager.rollback(status);
        }
    }

    /**
     * Get Item detail
     *
     * @param id
     * @return Item info with Categories
     */
    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public CategorizeItem getItemDetail(@PathVariable("id") String id) {
        CategorizeItem item = new CategorizeItem(itemRepository.getById(id));
        item.setCategories(categoryRepository.getByItemId(item.getId()));
        return item;
    }

    /**
     * Return Item list by specific order
     * @param orderId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/order/{id}")
    public List<Item> getAllByOrderId(@PathVariable("id") String orderId) {
        List<String> itemIdList = Lists.transform(orderItemRepository.getByOrderId(orderId), OrderItem::getItemId);
        return itemRepository.getByIdList(itemIdList);
    }

    /**
     * Return all items by specific company
     * @param companyId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/company/{id}")
    public List<Item> getAllByCompanyId(@PathVariable("id")String companyId) {
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

    /**
     * Toggle item for select
     * @param input
     * @throws ParseException
     */
    @RequestMapping(method = RequestMethod.POST,value = "/set/sale")
    public void saleToggle(@RequestBody String input) throws ParseException {
        JSONParser parser=new JSONParser();
        JSONObject main = (JSONObject) parser.parse(input);

        String itemId = main.get("itemId").toString();
        Boolean notForSale = (Boolean) main.get("notForSale");

        Item item = new Item();
        item.setId(itemId);
        item.setNotForSale(notForSale);
        itemRepository.updateSelectiveById(item);
    }


}

