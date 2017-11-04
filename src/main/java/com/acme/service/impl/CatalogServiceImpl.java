package com.acme.service.impl;

import com.acme.constant.Constants;
import com.acme.elasticsearch.repository.CatalogRepository;
import com.acme.model.CategoryItem;
import com.acme.model.Content;
import com.acme.model.Item;
import com.acme.model.ItemContent;
import com.acme.model.filter.CatalogFilter;
import com.acme.repository.*;
import com.acme.service.CatalogService;
import com.acme.service.ElasticService;
import com.acme.service.ItemService;
import com.acme.service.WishlistService;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ibm.icu.text.Transliterator;
import java.util.stream.Stream;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

/**
 * Created by nikolay on 13.08.17.
 */

@Service
public class CatalogServiceImpl implements CatalogService {

    private static String RUSSIAN_TO_LATIN_BGN = "Russian-Latin/BGN";

    @Autowired
    ElasticService elasticService;

    @Autowired
    ItemService itemService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CatalogRepository catalogRepository;

    @Autowired
    ItemContentRepository itemContentRepository;

    @Autowired
    CategoryItemRepository categoryItemRepository;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    WishlistService wishlistService;

    @Override
    public List<Item> getCatalog(CatalogFilter filter) {
        // получение изображения используемого по-умолчанию
        Content defContent = contentRepository.findOneByIsDefault(true);

        // если в фильтре отсутствует категория, то используем спецификацию
        List<Item> result = Strings.isNullOrEmpty(filter.getCategory()) ? itemService.getAll(filter) : itemService.getAllByCategory(filter);

        if(!Strings.isNullOrEmpty(filter.getClientEmail())){
            List<String> wishedItems = wishlistService.getWishedItems(filter.getClientEmail());
            if(!wishedItems.isEmpty()){
                result = result.stream().map(item -> {
                    item.setInWishlist(wishedItems.contains(item.getId()));
                    return item;
                }).collect(Collectors.toList());
            }
        }

        for (Item item : result) {
            itemService.fillItem(item, defContent);
        }
        return result;
    }

    @Override
    public void indexItems() {
        elasticService.indexItems(itemService.getAll());
    }

    @Override
    public void transliteItems(boolean all) {
        Stream<Item> items = itemService.getAll().stream();
        if(!all){
            // фильтруем если нужно
            items = items.filter(item -> Strings.isNullOrEmpty(item.getTransliteName()));

        }
        // обновляем
        items.forEach(item -> {
            item.setTransliteName(translite(item.getName()));
            itemService.updateItem(item);
        });
    }

    @Override
    public List<Item> searchItems(String criteria) {
        Content defContent = contentRepository.findOneByIsDefault(true);

        /* формируем запрос */
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(multiMatchQuery(criteria)
                        .field("name^10")
                        .field("name.stemmed^2")
                        .field("name.shingles^2")
                        .field("name.ngrams")
                        .operator(MatchQueryBuilder.Operator.AND)
                        //.fuzziness(Fuzziness.ONE)
                        .fuzziness(Fuzziness.ZERO)
                )
                .build();

        /* ищем документы */
        List<Item> result = Lists.newArrayList(catalogRepository.search(searchQuery));
        for (Item item : result) {
            itemService.fillItem(item, defContent);
        }
        return result;
    }

    @Override
    public Item getItemDetailById(String itemId) {
        Item item = itemService.getItem(itemId);
        return fillItem(item);
    }

    @Override
    public Item getItemDetailByTransliteName(String name) {
        return fillItem(itemService.getItemByLatinName(name));
    }

    /**
     *
     * @param item
     * @return
     */
    private Item fillItem(Item item){
        Content defContent = contentRepository.findOneByIsDefault(true);
        List<ItemContent> itemContents = itemContentRepository.findAllByItemId(item.getId());
        if (itemContents.isEmpty()) {
            item.setUrl(Constants.VIEW_URL + defContent.getId());
        } else {
            item.setItemContents(itemContents);
            Optional<ItemContent> contentOptional = itemContents.stream().filter(ItemContent::isMain).findFirst();
            if (contentOptional.isPresent()){
                item.setUrl(Constants.VIEW_URL + contentOptional.get().getContentId());
            } else {
                item.setUrl(Constants.VIEW_URL + defContent.getId());
            }
        }
        item.setCategories(categoryRepository.findByIdIn(categoryItemRepository.findAllByItemId(item.getId()).stream().map(CategoryItem::getCategoryId).collect(Collectors.toList())));
        return item;
    }

    /**
     *
     * @param input
     * @return
     */
    private String translite(String input){
        Transliterator russianToLatinNoAccentsTrans = Transliterator.getInstance(RUSSIAN_TO_LATIN_BGN);
        return russianToLatinNoAccentsTrans.transliterate(input).replaceAll("·|ʹ|\\.|\"|,|\\(|\\)", "").replaceAll("\\*|\\s+","-").toLowerCase();
    }
}
