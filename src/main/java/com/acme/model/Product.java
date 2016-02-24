package com.acme.model;

public class Product {

    Item item;
    String contentId;
    String imageUrl;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Product{" +
                "item=" + item +
                ", contentId='" + contentId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
