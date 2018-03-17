package com.acme.service;

import com.acme.model.dto.CatalogDetailDto;
import com.acme.model.dto.CatalogDto;
import com.acme.model.filter.CatalogFilter;

import java.util.List;
import java.util.Set;

/**
 * Created by nikolay on 13.08.17.
 */
public interface CatalogService {

    /**
     * Получение товара по фльтру
     * @param filter
     * @return
     */
    Set<CatalogDto> getCatalog(CatalogFilter filter);

    Set<CatalogDto> getBestsellers();

    Set<CatalogDto> searchItems(String criteria);

    void indexItems();

    void transliteItems(boolean all);

    CatalogDetailDto getItemDetailById(String itemId);

    /**
     *
     * @param name
     * @return
     */
    CatalogDetailDto getItemDetailByTransliteName(String name);
}
