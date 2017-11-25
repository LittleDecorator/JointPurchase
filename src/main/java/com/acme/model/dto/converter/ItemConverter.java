package com.acme.model.dto.converter;

import com.acme.model.Catalog;
import com.acme.model.Item;

public class ItemConverter {

    public static Catalog itemToCatalog(Item item){
        Catalog catalog = new Catalog();

        catalog.setId(item.getId());
        catalog.setAge(item.getAge());
        catalog.setArticle(item.getArticle());
        catalog.setBestseller(item.isBestseller());
        catalog.setCompany(item.getCompany());
        catalog.setDescription(item.getDescription());
        catalog.setMaterial(item.getMaterial());
        catalog.setName(item.getName());
        catalog.setPrice(item.getPrice());
        catalog.setCategories(item.getCategories());
        catalog.setTransliteName(item.getTransliteName());
        return catalog;
    }

}
