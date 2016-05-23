package com.acme.model.filter;

public class ItemFilter {

    String name;
    String companyId;
    String article;
    int limit;
    int offset;
    String categoryId;

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

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "ItemFilter{" +
                "name='" + name + '\'' +
                ", companyId='" + companyId + '\'' +
                ", article='" + article + '\'' +
                '}';
    }
}
