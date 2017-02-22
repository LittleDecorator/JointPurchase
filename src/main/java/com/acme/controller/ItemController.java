package com.acme.controller;

import com.acme.model.filter.ItemFilter;
import com.acme.repository.specification.ItemSpecifications;
import com.acme.repository.*;
import com.acme.service.CategoryService;
import com.acme.service.ItemService;
import com.acme.model.Item;
import com.google.common.collect.Lists;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    _CompanyRepository companyRepository;

    @Autowired
	ContentRepository contentRepository;

    @Autowired
	ItemContentRepository itemContentRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ItemService itemService;

    /**
     * Получение всех товаров по фильтру
     **/
    @RequestMapping(method = RequestMethod.GET)
    public List<Item> getItems(ItemFilter filter){
        /* выставляем offset, limit и order by */
        Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit(), Sort.Direction.DESC, "date_add");
        return Lists.newArrayList(itemRepository.findAll(ItemSpecifications.filter(filter), pageable).iterator());
    }

    /**
     * Создание нового | обновление существующего товара
     *
     * @param item
     * @return - ID of new item
     * @throws ParseException
     * @throws IOException
     */
    @RequestMapping(method = {RequestMethod.PUT,RequestMethod.POST})
    public String addItem(@RequestBody Item item) throws ParseException, IOException {
        String itemId = null;
        if (item != null) {
            TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
            try {
                itemId = itemRepository.save(item).getId();
                //TODO: разобраться как добавлять категории  и надо ли.
//                itemRepository.insertSelective(item);
                /* add new linked categories */
//                categoryItemRepository.insertBulk(categoryService.createCategoryItemList4Item(itemId, item.getCategories().stream().map(Category::getId).collect(Collectors.toList())));
                /* удаление не существующих связей */
                //                categoryItemRepository.deleteByItemAndExcludedCategoryIdList(item.getId(), categoryIdList);
//                categoryIdList.removeAll(categoryItemRepository.getByItemId(item.getId()).stream().map(CategoryItem::getCategoryId).collect(Collectors.toList()));
//                List<CategoryItem> categoryItems = categoryService.createCategoryItemList4Item(item.getId(),categoryIdList);
//                categoryItemRepository.insertBulk(categoryItems);

                transactionManager.commit(status);
            } catch (Exception ex) {
                ex.printStackTrace();
                transactionManager.rollback(status);
            }
        }
        return itemId;
    }

    /**
     * Удаление товара по ID
     * @param id
     */
    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public void deleteItem(@PathVariable("id") String id) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            //TODO: обязательно проверить
            Item item = itemRepository.findOne(id);
            orderItemRepository.delete(item.getOrderItems());
            itemContentRepository.delete(item.getItemContents());
//            item.getCategories()
//            categoryItemRepository.delete(item.getCategories());
            itemRepository.delete(id);
            transactionManager.commit(status);
        } catch (Exception ex){
            System.out.println(Arrays.toString(ex.getStackTrace()));
            transactionManager.rollback(status);
        }
    }

    /**
     * Получение товара по ID
     *
     * @param id
     * @return Item info with Categories
     */
    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public Item getItemDetail(@PathVariable("id") String id) {
        return itemRepository.findOne(id);
    }

    /**
     * Получение списка товаров по ID заказа
     * @param orderId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/order/{id}")
    public List<Item> getAllByOrderId(@PathVariable("id") String orderId) {
        return itemRepository.findByOrderId(orderId);
    }

    /**
     * Получение товара по компании
     * @param companyId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/company/{id}")
    public List<Item> getAllByCompanyId(@PathVariable("id")String companyId) {
        return itemRepository.findByCompanyId(companyId);
    }

    /**
     * Получение мапы товара для списков
     **/
    @RequestMapping(method = RequestMethod.GET,value = "/map")
    public List<ItemMap> getItemMap() {
        return itemRepository.findAllByOrderByDateAddAsc().stream().map(i -> new ItemMap(i.getId(), i.getName(), i.getPrice(), i.getArticle())).collect(Collectors.toList());
    }

    /**
     * Зменение признака товара "выставить на продажу"
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
        itemRepository.save(item);
    }

    /*------ NESTED -------*/

    private class ItemMap {
        String id;
        String name;
        double price;
        String article;

        ItemMap(String id, String name, double price, String article) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.article = article;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getArticle() {
            return article;
        }

        public void setArticle(String article) {
            this.article = article;
        }
    }
}

