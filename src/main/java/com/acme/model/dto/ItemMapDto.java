package com.acme.model.dto;

import com.acme.model.Sale;

public class ItemMapDto {

    private String id;
    private String name;
    private double price;
    private String article;
    private Sale sale;

    public ItemMapDto(String id, String name, double price, String article) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.article = article;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }
}
