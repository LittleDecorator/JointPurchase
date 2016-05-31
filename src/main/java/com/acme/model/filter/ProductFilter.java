package com.acme.model.filter;

public class ProductFilter {

    String itemId;
    String category;
    String company;
    Integer limit;
    Integer offset;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "ProductFilter{" +
                "itemId='" + itemId + '\'' +
                ", category='" + category + '\'' +
                ", company='" + company + '\'' +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
