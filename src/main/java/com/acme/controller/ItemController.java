package com.acme.controller;

import com.acme.elasticsearch.repository.CatalogRepository;
import com.acme.model.*;
import com.acme.model.dto.CatalogDto;
import com.acme.model.dto.ItemDto;
import com.acme.model.dto.ItemMapDto;
import com.acme.model.dto.mapper.ItemMapper;
import com.acme.model.filter.ItemFilter;
import com.acme.repository.specification.ItemSpecifications;
import com.acme.repository.*;
import com.acme.repository.specification.SpecificationBuilder;
import com.acme.service.CategoryService;
import com.acme.service.ItemService;
import javax.validation.constraints.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
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

    @Autowired
    private ItemMapper itemMapper;

    /**
     * Получение всех товаров по фильтру
     **/
    @RequestMapping(method = RequestMethod.GET)
    public List<ItemDto> getItems(ItemFilter filter) {
        Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit(), Sort.Direction.DESC, "dateAdd", "name");
        List<Item> itemList = itemRepository.findAll(SpecificationBuilder.applyItemFilter(filter), pageable).getContent();
        return itemMapper.toDto(itemList);
    }

    /**
     * Создание нового | обновление существующего товара
     *
     * @param item
     * @return - ID of new item
     * @throws ParseException
     * @throws IOException
     */
    @Transactional
    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.POST})
    public String addItem(@NotNull @RequestBody Item item) throws ParseException, IOException {
        itemRepository.save(item);
        // поместим|обновим в индекс только после добавления товара
        catalogRepository.save(item);
        return item.getId();
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
            /* удалим записи товара в заказах */
            orderItemRepository.deleteByIdItemId(id);
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
        return itemRepository.findByIdIn(order.getOrderItems().stream().map(oi -> oi.getId().getItemId()).collect(Collectors.toList()));
    }

    /**
     * Получение товара по компании
     *
     * @param companyId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/company/{id}")
    public List<Item> getAllByCompanyId(@PathVariable("id") String companyId) {
        return itemRepository.findByCompanyId(companyId);
    }

    /**
     * Получение мапы товара для списков
     **/
    @RequestMapping(method = RequestMethod.GET, value = "/map")
    public List<ItemMapDto> getItemMap(ItemFilter filter) {
        List<Item> items = itemRepository.findAll(SpecificationBuilder.applyItemFilter(filter));
        return items.stream().map(i -> {
            ItemMapDto itemMap = new ItemMapDto(i.getId(), i.getName(), i.getPrice(), i.getArticle());
            itemMap.setSale(i.getSale());
            return itemMap;
        }).collect(Collectors.toList());
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
    public Collection<CatalogDto> refreshCart(@RequestBody List<String> itemIds){
        List<Item> items = itemRepository.findAll(itemIds);
        return itemMapper.toCatalogDto(items);
    }

}

