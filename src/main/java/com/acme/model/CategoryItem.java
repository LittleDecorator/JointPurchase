package com.acme.model;


import java.util.Date;

public class CategoryItem {

    private String id;

    private String categoryId;

    private String itemId;

    private Date dateAdd;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId == null ? null : categoryId.trim();
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId == null ? null : itemId.trim();
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

    @Override
    public String toString() {
        return "CategoryItem{" +
                "id='" + id + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", dateAdd=" + dateAdd +
                '}';
    }
}