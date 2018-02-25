package com.acme.controller;

import com.acme.model.dto.CatalogDetailDto;
import com.acme.model.dto.CatalogDto;
import com.acme.model.filter.CatalogFilter;
import com.acme.service.CatalogService;
import com.pushtorefresh.javac_warning_annotation.Warning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller that response for item representation for client.
 */
@RestController
@RequestMapping(value = "/api/catalog")
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
    public List<CatalogDto> getCategoriesPreviewItems(@RequestBody CatalogFilter filter) throws Exception {
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
     * Create translite for items
     * @param all
     */
    @RequestMapping(method = RequestMethod.PATCH, value = "translite")
    public void transliteItems(@RequestParam(name = "all", required = false, defaultValue = "true") Boolean all){
        catalogService.transliteItems(all);
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
    public List<CatalogDto> searchItem(@RequestParam(value = "criteria") String criteria) {
        return catalogService.searchItems(criteria);
    }

    /**
     * Получение детальной информации по конкретному товару
     * Like ItemCard, but accept by client
     *
     * @param name
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET, value = "{name}/detail")
    public CatalogDetailDto getItemDetail(@PathVariable("name") String name) throws Exception {
        return catalogService.getItemDetailByTransliteName(name);
    }

    @RequestMapping(method = RequestMethod.GET, value = "best")
    public List<CatalogDto> getBestSellers() throws Exception {
        return catalogService.getBestsellers();
    }
}
