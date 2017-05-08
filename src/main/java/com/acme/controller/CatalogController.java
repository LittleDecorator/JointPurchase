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
import java.util.function.Function;
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
            itemService.fillItem(item, defContent);
        }
        return Lists.newArrayList(items);
    }

    /**
     * индексация документов
     */
    @RequestMapping(method = RequestMethod.POST, value = "index")
    public void indexItem(){
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
    public List<Item> searchItem(@RequestParam(value = "criteria") String criteria) {

        Content defContent = contentRepository.findOneByIsDefault(true);

        /* Указываем в каких полях с каким приоритетом */
        SimpleQueryStringBuilder builder = new SimpleQueryStringBuilder(criteria);
        builder.field("name",4).field("title",3).field("tags",2).field("content");

        /* ищем документы */
        List<Item> result = Lists.newArrayList(catalogRepository.search(builder));
        for(Item item : result){
            itemService.fillItem(item, defContent);
        }
        return result;
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
            item.setUrl(Constants.VIEW_URL+defContent.getId());
        } else {
            item.setItemContents(itemContents);
            item.setUrl(Constants.VIEW_URL + itemContents.stream().filter(ItemContent::isMain).findFirst().get().getContentId());
        }
        item.setCategories(categoryRepository.findByIdIn(categoryItemRepository.findAllByItemId(item.getId()).stream().map(CategoryItem::getCategoryId).collect(Collectors.toList())));
        return item;
    }

}
