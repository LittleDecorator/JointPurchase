package com.acme.model.dto;

import com.acme.model.Item;

public class ItemSearchResult {

    private Item item;
    private String categoryId;
    private String contentId;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    @Override
    public String toString() {
        return "ItemSearchResult{" +
                "item=" + item +
                ", categoryId='" + categoryId + '\'' +
                ", contentId='" + contentId + '\'' +
                '}';
    }
}
