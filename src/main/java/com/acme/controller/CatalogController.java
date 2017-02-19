package com.acme.controller;

import com.acme.constant.Constants;
import com.acme.model.Content;
import com.acme.model.Item;
import com.acme.model.ItemContent;
import com.acme.model.dto.SearchResultElement;
import com.acme.model.filter.ItemFilter;
import com.acme.model.specification.ItemSpecifications;
import com.acme.repository.*;
import com.acme.service.ItemService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pushtorefresh.javac_warning_annotation.Warning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/catalog")
public class CatalogController {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    ItemService itemService;

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
        /* зачистим ссылки */
        defContent.setItemContents(null);
        defContent.setUrl(Constants.PREVIEW_URL+defContent.getId());
        /* выставляем offset, limit и order by */
        Pageable pageable = new OffsetBasePage(filter.getOffset(), filter.getLimit());
        Page<Item> items = itemRepository.findAll(ItemSpecifications.filter(filter), pageable);
        for (Item item : items){
            List<ItemContent> contentList = item.getItemContents();
            if(!contentList.isEmpty()){
                for(ItemContent itemContent : item.getItemContents()){
                    Content content = itemContent.getContent();
                    content.setUrl(Constants.PREVIEW_URL+content.getId());
                }
            } else {
                ItemContent itemContent = new ItemContent();
                itemContent.setContent(defContent);
                itemContent.setMain(true);
                itemContent.setShow(true);
                contentList.add(itemContent);
            }
        }
        return Lists.newArrayList(items);
    }

    /**
     * Полнотекстный поиск.
     * Сечас не работает, должен возвращать результат через
     * Elasticsearch
     *
     * @param criteria
     * @return
     */
    @Warning(value = "Not working temporary")
    @RequestMapping(method = RequestMethod.GET,value = "search")
    public Collection<SearchResultElement> searchItem(@RequestParam(value = "criteria") String criteria) {
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
        return stash.values();
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
        /* зачистим ссылки */
        defContent.setItemContents(null);
        defContent.setUrl(Constants.PREVIEW_URL+defContent.getId());
        Item item = itemRepository.findOne(itemId);
        if(item.getItemContents().isEmpty()){
            ItemContent itemContent = new ItemContent();
            itemContent.setContent(defContent);
            itemContent.setMain(true);
            itemContent.setShow(true);
            item.getItemContents().add(itemContent);
        }
        return item;
    }

}
