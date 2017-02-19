package com.acme.model.filter;

public class ItemFilter {

    private String name;
    private String article;
    private String company;
    private String category;
    private int limit;
    private int offset;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "ItemFilter{" +
                "name='" + name + '\'' +
                ", company='" + company + '\'' +
                ", article='" + article + '\'' +
                '}';
    }
}
