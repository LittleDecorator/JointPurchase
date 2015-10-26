package com.acme.model.domain;

import com.acme.gen.domain.Category;
import com.acme.gen.domain.Item;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ItemCategoryLink {

    private String id;
    private String name;
    private String companyId;
    private String article;
    private String description;
    private Integer inStock;
    private BigDecimal price;
    private Date dateAdd;
    private boolean notForSale;

    public ItemCategoryLink() {}

    public ItemCategoryLink(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.companyId = item.getCompanyId();
        this.article = item.getArticle();
        this.description = item.getDescription();
        this.price = item.getPrice();
        this.dateAdd = item.getDateAdd();
        this.inStock = item.getInStock();
    }

    List<Category> categories;

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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Integer getInStock() {
        return inStock;
    }

    public void setInStock(Integer inStock) {
        this.inStock = inStock;
    }

    @Override
    public String toString() {
        return "ItemCategoryLink{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", companyId='" + companyId + '\'' +
                ", article='" + article + '\'' +
                ", description='" + description + '\'' +
                ", inStock=" + inStock +
                ", price=" + price +
                ", dateAdd=" + dateAdd +
                ", notForSale=" + notForSale +
                ", categories=" + categories +
                '}';
    }
}
