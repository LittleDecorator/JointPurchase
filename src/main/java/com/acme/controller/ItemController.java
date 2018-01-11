package com.acme.controller;

import com.acme.elasticsearch.repository.CatalogRepository;
import com.acme.enums.OrderStatus;
import com.acme.model.*;
import com.acme.model.dto.ItemMapDto;
import com.acme.model.filter.ItemFilter;
import com.acme.repository.specification.ItemSpecifications;
import com.acme.repository.*;
import com.acme.service.CategoryService;
import com.acme.service.ItemService;
import com.google.common.collect.Lists;
import javax.servlet.http.HttpServletRequest;
import org.assertj.core.util.Strings;
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
@RequestMapping(value = "/api/item")
public class ItemController {

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
        Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit(), Sort.Direction.DESC, "dateAdd", "name");
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
    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.POST})
    public String addItem(@RequestBody Item item) throws ParseException, IOException {
        String itemId = null;
        if (item != null) {
            itemId = itemRepository.save(item).getId();
            // поместим|обновим в индекс только после добавления товара
            catalogRepository.save(item);
        }
        return itemId;
    }

    /**
     * Удаление товара по ID
     *
     * @param id
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public void deleteItem(@PathVariable("id") String id) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            /* удалим записи товара в заказе */
            orderItemRepository.deleteByItemId(id);
            /* удалим записи товара в изображениях */
            itemContentRepository.deleteByItemId(id);
            /* удалим записи товара в категориях */
            categoryItemRepository.deleteByIdItemId(id);
            /* удалим товар */
            itemRepository.delete(id);
            transactionManager.commit(status);
        } catch (Exception ex) {
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
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public Item getItemDetail(@PathVariable("id") String id) {
        return itemRepository.findOne(id);
    }

    /**
     * Получение списка товаров по ID заказа
     *
     * @param orderId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/order/{id}")
    public List<Item> getAllByOrderId(@PathVariable("id") String orderId) {
        Order order = orderRepository.findOne(orderId);
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
        List<Item> items = itemRepository.findByIdIn(orderItems.stream().map(OrderItem::getItemId).collect(Collectors.toList()));
        for (Item item : items) {
            item.setCategories(categoryRepository.findByIdIn(categoryItemRepository.findAllByIdItemId(item.getId()).stream().map(ci->ci.getId().getCategoryId()).collect(Collectors.toList())));
        }
        return items;
    }

    /**
     * Получение товара по компании
     *
     * @param companyId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/company/{id}")
    public List<Item> getAllByCompanyId(@PathVariable("id") String companyId) {
        List<Item> items = itemRepository.findByCompanyId(companyId);
        for (Item item : items) {
            item.setCategories(categoryRepository.findByIdIn(categoryItemRepository.findAllByIdItemId(item.getId()).stream().map(ci->ci.getId().getCategoryId()).collect(Collectors.toList())));
        }
        return items;
    }

    /**
     * Получение мапы товара для списков
     **/
    @RequestMapping(method = RequestMethod.GET, value = "/map")
    public List<ItemMapDto> getItemMap(@RequestParam(name = "name", required = false) String name, @RequestParam(value = "article", required = false) String article) {
        List<Item> items = itemRepository.findAll(ItemSpecifications.modalFilter(name, article));
        List<ItemMapDto> result = items.stream().map(i -> {
            ItemMapDto itemMap = new ItemMapDto(i.getId(), i.getName(), i.getPrice(), i.getArticle());
            itemMap.setSale(i.getSale());
            return itemMap;
        }).collect(Collectors.toList());
        return result;
    }

    /**
     * Зменение признака товара "выставить на продажу"
     *
     * @param input
     * @throws ParseException
     */
    @RequestMapping(method = RequestMethod.POST, value = "/set/sale")
    public void saleToggle(@RequestBody String input) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject main = (JSONObject) parser.parse(input);

        String itemId = main.get("itemId").toString();
        Boolean notForSale = (Boolean) main.get("notForSale");

        Item item = new Item();
        item.setId(itemId);
        item.setNotForSale(notForSale);
        itemRepository.save(item);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/refresh")
    public Collection<Item> refreshCart(@RequestBody List<Item> items){
        Map<String, Item> map = items.stream().collect(Collectors.toMap(Item::getId, Function.identity()));
        for(Item item : itemRepository.findAll(map.keySet())){
            Sale sale = item.getSale();
            Date now = new Date();
            Item cartItem = map.get(item.getId());
            if(sale !=null && sale.isActive() && sale.getStartDate().before(now) && sale.getEndDate().after(now)){
                cartItem.setSale(sale);
                cartItem.setPrice(item.getPrice());
                cartItem.setSalePrice(((Float)(item.getPrice() - (item.getSale().getDiscount() / 100f * item.getPrice()))).intValue());
            } else {
                cartItem.setSale(null);
                cartItem.setSalePrice(null);
            }
        }
        return map.values();
    }

}

