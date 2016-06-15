package com.acme.model;

import java.util.List;

public class CategorizeItem extends Item {

    List<Category> categories;

    public CategorizeItem() {}

    public CategorizeItem(Item item) {
        super(item);
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }


    @Override
    public String toString() {
        return "CategorizeItem{" +
                "categories=" + categories +
                "} " + super.toString();
    }
}
