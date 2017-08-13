package com.acme.controller;

import com.acme.model.Item;
import com.acme.model.filter.CatalogFilter;
import com.acme.service.CatalogService;
import com.pushtorefresh.javac_warning_annotation.Warning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/catalog")
public class CatalogController {

    @Autowired
    CatalogService catalogService;

    /**
     * Получение списка товаров по фильтру, для отображения на странице каталога.
     * Используемый фильтр в будующем должн быть расширен для учета цен и прочих характеристик
     *
     * @param filter
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.POST)
    public List<Item> getCategoriesPreviewItems(@RequestBody CatalogFilter filter) throws Exception {
        return catalogService.getCatalog(filter);
    }

    /**
     * индексация документов
     */
    @RequestMapping(method = RequestMethod.POST, value = "index")
    public void indexItem() {
        catalogService.indexItems();
    }

    /**
     * Полнотекстный поиск.
     * <p>
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
        return catalogService.searchItems(criteria);
    }

    /**
     * Получение детальной информации по конкретному товару
     *
     * @param itemId
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET, value = "{id}/detail")
    public Item getItemDetail(@PathVariable("id") String itemId) throws Exception {
        return catalogService.getItemDetail(itemId);
    }

}
