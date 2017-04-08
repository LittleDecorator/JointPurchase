package com.acme.controller;

import com.acme.constant.Constants;
import com.acme.elasticsearch.repository.CatalogRepository;
import com.acme.model.CategoryItem;
import com.acme.model.Content;
import com.acme.model.Item;
import com.acme.model.ItemContent;
import com.acme.model.dto.SearchResultElement;
import com.acme.model.filter.ItemFilter;
import com.acme.repository.specification.ItemSpecifications;
import com.acme.repository.*;
import com.acme.service.ElasticService;
import com.acme.service.ItemService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pushtorefresh.javac_warning_annotation.Warning;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/catalog")
public class CatalogController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ItemContentRepository itemContentRepository;

    @Autowired
    private CategoryItemRepository categoryItemRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ElasticService elasticService;

    /**
     * Получение списка товаров по фильтру, для отображения на странице каталога.
     * Используемый фильтр в будующем должн быть расширен для учета цен и прочих характеристик
     *
     * @param filter
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.POST)
    public List<Item> getCategoriesPreviewItems(@RequestBody ItemFilter filter) throws Exception {
        Content defContent = contentRepository.findOneByIsDefault(true);
        /* выставляем offset, limit и order by */
        Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit());
        Page<Item> items = itemRepository.findAll(ItemSpecifications.filter(filter), pageable);
        for (Item item : items){
            List<ItemContent> itemContents = itemContentRepository.findAllByItemId(item.getId());
            if(itemContents.isEmpty()){
                item.setUrl(Constants.PREVIEW_URL+defContent.getId());
            } else {
                item.setItemContents(itemContents);
                item.setUrl(Constants.PREVIEW_URL + itemContents.stream().filter(ItemContent::isMain).findFirst().get().getContentId());
            }
            item.setCategories(categoryRepository.findByIdIn(categoryItemRepository.findAllByItemId(item.getId()).stream().map(CategoryItem::getCategoryId).collect(Collectors.toList())));
        }
        return Lists.newArrayList(items);
    }

    /**
     * индексация документов
     */
    @RequestMapping(method = RequestMethod.POST, value = "index")
    public void indexItem() {
        elasticService.indexItems(itemRepository.findAll());
    }

    /**
     * Полнотекстный поиск.
     *
     * Сейчас осуществляет поиск только по полю NAME.
     * Необходимо довавить not_analyze поиск по категориям.
     * Elasticsearch
     *
     * @param criteria
     * @return
     */
    @Warning(value = "Not working temporary")
    @RequestMapping(method = RequestMethod.GET, value = "search")
//    public Collection<SearchResultElement> searchItem(@RequestParam(value = "criteria") String criteria) {
    public List<Item> searchItem(@RequestParam(value = "criteria") String criteria) {

        /* Указываем в каких полях с каким приоритетом */
        SimpleQueryStringBuilder builder = new SimpleQueryStringBuilder(criteria);
        builder.field("name",4).field("title",3).field("tags",2).field("content");

        /* ищем документы */
        List<Item> itemsList = Lists.newArrayList(catalogRepository.search(builder));


//        List<Product> items = customRepository.getBySearch(criteria);
//        Map<String,Product> productMap = items.stream().collect(Collectors.toMap(Product::getId, Function.<Product>identity()));
//        List<CategoryItem> categoryItems = itemCategoryLinkRepository.getByItemIdList(items.stream().map(Product::getId).collect(Collectors.toList()));

        Map<String,SearchResultElement> stash = Maps.newHashMap();

//        for(CategoryItem entry : categoryItems){
//            Product product = productMap.get(entry.getItemId());
//            product.setImageUrl("/media/image/gallery/" + product.getContentId());
//            if(stash.containsKey(entry.getCategoryId())){
//                stash.get(entry.getCategoryId()).getChildren().add(product);
//            } else {
//                SearchResultElement resultElement = new SearchResultElement();
//                resultElement.setGroupId(entry.getCategoryId());
//                resultElement.setGroupName(entry.getCategoryName());
//                resultElement.setChildren(Lists.newArrayList(product));
//                stash.put(entry.getCategoryId(),resultElement);
//            }
//        }
//        return stash.values();
        return itemsList;
    }

    /**
     * Получение детальной информации по конкретному товару
     * @param itemId
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET,value = "{id}/detail")
    public Item getItemDetail(@PathVariable("id") String itemId) throws Exception {
        Content defContent = contentRepository.findOneByIsDefault(true);
        Item item = itemRepository.findOne(itemId);
        List<ItemContent> itemContents = itemContentRepository.findAllByItemId(item.getId());
        if(itemContents.isEmpty()){
            item.setUrl(Constants.PREVIEW_URL+defContent.getId());
        } else {
            item.setItemContents(itemContents);
            item.setUrl(Constants.PREVIEW_URL + itemContents.stream().filter(ItemContent::isMain).findFirst().get().getContentId());
        }
        item.setCategories(categoryRepository.findByIdIn(categoryItemRepository.findAllByItemId(item.getId()).stream().map(CategoryItem::getCategoryId).collect(Collectors.toList())));
        return item;
    }

}
