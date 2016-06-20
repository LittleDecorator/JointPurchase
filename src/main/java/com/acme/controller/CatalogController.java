package com.acme.controller;

import com.acme.constant.Constants;
import com.acme.model.CategoryItem;
import com.acme.model.Item;
import com.acme.model.dto.ItemMediaTransfer;
import com.acme.model.dto.Product;
import com.acme.model.dto.SearchResultElement;
import com.acme.model.filter.CatalogFilter;
import com.acme.repository.CategoryRepository;
import com.acme.repository.CustomRepository;
import com.acme.repository.ItemCategoryLinkRepository;
import com.acme.repository.ItemRepository;
import com.acme.service.ItemService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/catalog")
public class CatalogController {

    @Autowired
    CustomRepository customRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemCategoryLinkRepository itemCategoryLinkRepository;

    @Autowired
    ItemService itemService;


    @RequestMapping(method = RequestMethod.POST)
    public List<Product> getCategoriesPreviewItems(@RequestBody CatalogFilter filter) throws Exception {
        //TODO: add filter reference to select
        List<Product> list = customRepository.getCatalog(filter);
        for(Product product : list){
            product.setImageUrl(Constants.PREVIEW_URL+product.getContentId());
        }

        return list;
    }

    @RequestMapping(method = RequestMethod.GET,value = "search")
    public Collection<SearchResultElement> searchItem(@RequestParam(value = "criteria") String criteria) {
        List<Product> items = customRepository.getBySearch(criteria);
        Map<String,Product> productMap = items.stream().collect(Collectors.toMap(Product::getId, Function.<Product>identity()));
        List<CategoryItem> categoryItems = itemCategoryLinkRepository.getByItemIdList(items.stream().map(Product::getId).collect(Collectors.toList()));

        Map<String,SearchResultElement> stash = Maps.newHashMap();

        for(CategoryItem entry : categoryItems){
            Product product = productMap.get(entry.getItemId());
            product.setImageUrl("/media/image/gallery/" + product.getContentId());
            if(stash.containsKey(entry.getCategoryId())){
                stash.get(entry.getCategoryId()).getChildren().add(product);
            } else {
                SearchResultElement resultElement = new SearchResultElement();
                resultElement.setGroupId(entry.getCategoryId());
                resultElement.setGroupName(entry.getCategoryName());
                resultElement.setChildren(Lists.newArrayList(product));
                stash.put(entry.getCategoryId(),resultElement);
            }
        }
        return stash.values();
    }

    @RequestMapping(method = RequestMethod.GET,value = "{id}/detail")
    public ItemMediaTransfer getItemDetail(@PathVariable("id") String itemId) throws Exception {
        Item item = itemRepository.getById(itemId);
        return itemService.getItemMediaTransfers(item);
    }

}
