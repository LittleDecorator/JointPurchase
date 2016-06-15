package com.acme.model;

import java.math.BigDecimal;
import java.util.Date;

public class Item {

    private String id;

    private String name;

    private String companyId;

    private String article;

    private String description;

    private BigDecimal price;

    private Date dateAdd;

    private boolean notForSale;

    private Integer inStock;

    public Item() {}

    public Item(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.companyId = item.getCompanyId();
        this.article = item.getArticle();
        this.description = item.getDescription();
        this.price = item.getPrice();
        this.dateAdd = item.getDateAdd();
        this.notForSale = item.isNotForSale();
        this.inStock = item.getInStock();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getCompanyId() {
        return companyId;
    }
    public void setCompanyId(String companyId) {
        this.companyId = companyId == null ? null : companyId.trim();
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article == null ? null : article.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

    public boolean isNotForSale() {
        return notForSale;
    }

    public void setNotForSale(boolean notForSale) {
        this.notForSale = notForSale;
    }

    public Integer getInStock() {
        return inStock;
    }

    public void setInStock(Integer inStock) {
        this.inStock = inStock;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", companyId='" + companyId + '\'' +
                ", article='" + article + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", dateAdd=" + dateAdd +
                ", notForSale=" + notForSale +
                ", inStock=" + inStock +
                '}';
    }
}