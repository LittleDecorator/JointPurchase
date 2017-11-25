package com.acme.service;

import com.acme.model.Catalog;
import com.acme.model.Item;
import com.acme.model.filter.CatalogFilter;

import java.util.List;

/**
 * Created by nikolay on 13.08.17.
 */
public interface CatalogService {

    List<Catalog> getCatalog(CatalogFilter filter);

    List<Item> getBestsellers();

    List<Item> searchItems(String criteria);

    void indexItems();

    void transliteItems(boolean all);

    Item getItemDetailById(String itemId);

    Item getItemDetailByTransliteName(String name);
}
