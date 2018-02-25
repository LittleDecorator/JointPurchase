package com.acme.service.impl;

import com.acme.elasticsearch.repository.CatalogRepository;
import com.acme.model.Item;
import com.acme.model.dto.CatalogDetailDto;
import com.acme.model.dto.CatalogDto;
import com.acme.model.dto.mapper.ItemMapper;
import com.acme.model.filter.CatalogFilter;
import com.acme.repository.*;
import com.acme.repository.specification.ItemSpecifications;
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

    @Autowired
    ItemMapper itemMapper;

    @Override
    public List<CatalogDto> getCatalog(CatalogFilter filter) {
        List<Item> result = itemService.getAll(filter);

        if(!Strings.isNullOrEmpty(filter.getClientEmail())){
            List<String> wishedItems = wishlistService.getWishedItems(filter.getClientEmail());
            if(!wishedItems.isEmpty()){
                result = result.stream().peek(item -> item.setInWishlist(wishedItems.contains(item.getId()))).collect(Collectors.toList());
            }
        }
        return itemMapper.toCatalogDto(result);
    }

    @Override
    public List<CatalogDto> getBestsellers() {
        List<Item> items = itemService.getAllBySpec(ItemSpecifications.isPopular());
        return itemMapper.toCatalogDto(items);
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
            // транслит строиться по имени компании и названию товара
            item.setTransliteName(translite(item.getCompany().getName() + " " + item.getName()));
            itemService.updateItem(item);
        });
    }

    @Override
    public List<CatalogDto> searchItems(String criteria) {
        //Content defContent = contentRepository.findOneByIsDefault(true);

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
        List<Item> items = Lists.newArrayList(catalogRepository.search(searchQuery));
        return itemMapper.toCatalogDto(items);
    }

    @Override
    public CatalogDetailDto getItemDetailById(String itemId) {
        return itemMapper.toDetailDto(itemService.getItem(itemId));
    }

    @Override
    public CatalogDetailDto getItemDetailByTransliteName(String name) {
        return itemMapper.toDetailDto(itemService.getItemByLatinName(name));
    }

    /**
     *
     * @param item
     * @return
     */
    //private Item fillItem(Item item){
    //    Content defContent = contentRepository.findOneByIsDefault(true);
    //    List<ItemContent> itemContents = itemContentRepository.findAllByItemId(item.getId());
    //    if (itemContents.isEmpty()) {
    //        item.setUrl(Constants.VIEW_URL + defContent.getId());
    //    } else {
    //        item.setItemContents(itemContents);
    //        Optional<ItemContent> contentOptional = itemContents.stream().filter(ItemContent::isMain).findFirst();
    //        if (contentOptional.isPresent()){
    //            item.setUrl(Constants.VIEW_URL + contentOptional.get().getContentId());
    //        } else {
    //            item.setUrl(Constants.VIEW_URL + defContent.getId());
    //        }
    //    }
    //    item.setCategories(categoryRepository.findByIdIn(categoryItemRepository.findAllByIdItemId(item.getId()).stream().map(ci-> ci.getId().getCategoryId()).collect(Collectors.toList())));
    //    // TODO: если время акции не настало, то мы не должны её вообще получать для товара
    //    Sale sale = item.getSale();
    //    Date now = new Date();
    //    if(sale !=null && sale.getStartDate().before(now) && sale.getEndDate().after(now)){
    //        item.setSalePrice(((Float)(item.getPrice() - (item.getSale().getDiscount() / 100f * item.getPrice()))).intValue());
    //    }
    //    return item;
    //}

    /**
     *
     * @param input
     * @return
     */
    private String translite(String input){
        Transliterator russianToLatinNoAccentsTrans = Transliterator.getInstance(RUSSIAN_TO_LATIN_BGN);
        return russianToLatinNoAccentsTrans.transliterate(input)
            .replaceAll("·|ʹ|\\.|\"|,|\\(|\\)", "")
            .replaceAll("\\*|\\s+","-")
            .toLowerCase();
    }
}
