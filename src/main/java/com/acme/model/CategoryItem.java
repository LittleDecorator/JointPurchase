//package com.acme.model;
//
//
//import javax.persistence.Entity;
//import javax.persistence.Table;
//import java.util.Date;
//
//@Entity
//@Table(name = "category_item")
//public class CategoryItem {
//
//    private String id;
//
//    private String categoryId;
//
//    private String categoryName;
//
//    private String itemId;
//
//    private Date dateAdd;
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id == null ? null : id.trim();
//    }
//
//    public String getCategoryId() {
//        return categoryId;
//    }
//
//    public void setCategoryId(String categoryId) {
//        this.categoryId = categoryId == null ? null : categoryId.trim();
//    }
//
//    public String getItemId() {
//        return itemId;
//    }
//
//    public void setItemId(String itemId) {
//        this.itemId = itemId == null ? null : itemId.trim();
//    }
//
//    public Date getDateAdd() {
//        return dateAdd;
//    }
//
//    public void setDateAdd(Date dateAdd) {
//        this.dateAdd = dateAdd;
//    }
//
//    public String getCategoryName() {
//        return categoryName;
//    }
//
//    public void setCategoryName(String categoryName) {
//        this.categoryName = categoryName;
//    }
//
//    @Override
//    public String toString() {
//        return "CategoryItem{" +
//                "id='" + id + '\'' +
//                ", categoryId='" + categoryId + '\'' +
//                ", categoryName='" + categoryName + '\'' +
//                ", itemId='" + itemId + '\'' +
//                ", dateAdd=" + dateAdd +
//                '}';
//    }
//}