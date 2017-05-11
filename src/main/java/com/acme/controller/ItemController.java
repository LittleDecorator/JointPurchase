package com.acme.controller;

import com.acme.elasticsearch.repository.CatalogRepository;
import com.acme.enums.OrderStatus;
import com.acme.model.*;
import com.acme.model.filter.ItemFilter;
import com.acme.repository.specification.ItemSpecifications;
import com.acme.repository.*;
import com.acme.service.CategoryService;
import com.acme.service.ItemService;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/item")
public class ItemController{

    @Autowired
	ItemRepository itemRepository;

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
	OrderItemRepository orderItemRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
	ContentRepository contentRepository;

    @Autowired
	ItemContentRepository itemContentRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CategoryItemRepository categoryItemRepository;

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
    public List<Item> getItems(ItemFilter filter) {
        /* выставляем offset, limit и order by */
        Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit(), Sort.Direction.DESC, "dateAdd","name");
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
                //добавим сам товар
                itemId = itemRepository.save(item).getId();
                List<String> categoryIdList = item.getCategories().stream().map(Category::getId).collect(Collectors.toList());
                /* удаление не существующих связей */
                categoryItemRepository.deleteByItemIdAndCategoryIdNotIn(item.getId(), categoryIdList);
                /* актуализация списка сатегорий */
                categoryIdList.removeAll(categoryItemRepository.findAllByItemId(item.getId()).stream().map(CategoryItem::getCategoryId).collect(Collectors.toList()));
                /* добавление новых связей */
                categoryItemRepository.save(categoryService.createCategoryItemList4Item(itemId, categoryIdList));
                transactionManager.commit(status);
                // поместим в индекс только после добавления товара
                catalogRepository.save(item);
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
            /* удалим записи товара в заказе */
            orderItemRepository.deleteByItemId(id);
            /* удалим записи товара в изображениях */
            itemContentRepository.deleteByItemId(id);
            /* удалим записи товара в категориях */
            categoryItemRepository.deleteByItemId(id);
            /* удалим товар */
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
        Item item = itemRepository.findOne(id);
        item.setCategories(categoryRepository.findByIdIn(categoryItemRepository.findAllByItemId(item.getId()).stream().map(CategoryItem::getCategoryId).collect(Collectors.toList())));
        return item;
    }

    /**
     * Получение списка товаров по ID заказа
     * @param orderId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/order/{id}")
    public List<Item> getAllByOrderId(@PathVariable("id") String orderId) {
        Order order = orderRepository.findOne(orderId);
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
        List<Item> items = itemRepository.findByIdIn(orderItems.stream().map(OrderItem::getItemId).collect(Collectors.toList()));
        for(Item item : items){
            item.setCategories(categoryRepository.findByIdIn(categoryItemRepository.findAllByItemId(item.getId()).stream().map(CategoryItem::getCategoryId).collect(Collectors.toList())));
        }
        return items;
    }

    /**
     * Получение товара по компании
     * @param companyId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/company/{id}")
    public List<Item> getAllByCompanyId(@PathVariable("id")String companyId) {
        List<Item> items = itemRepository.findByCompanyId(companyId);
        for(Item item : items){
            item.setCategories(categoryRepository.findByIdIn(categoryItemRepository.findAllByItemId(item.getId()).stream().map(CategoryItem::getCategoryId).collect(Collectors.toList())));
        }
        return items;
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

