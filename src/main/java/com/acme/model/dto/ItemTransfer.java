package com.acme.model.dto;

import com.acme.model.Item;

import java.util.List;

public class ItemTransfer {

    Item item;
    List<String> categories;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "ItemTransfer{" +
                "item=" + item +
                ", categories=" + categories +
                '}';
    }
}
