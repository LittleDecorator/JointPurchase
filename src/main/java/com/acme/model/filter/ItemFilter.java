package com.acme.model.filter;

import com.acme.model.Company;

public class ItemFilter {

    private String name;
    private String article;
    private Company company;
    private int limit;
    private int offset;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
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

    @Override
    public String toString() {
        return "ItemFilter{" +
               "name='" + name + '\'' +
               ", article='" + article + '\'' +
               ", company=" + company +
               ", limit=" + limit +
               ", offset=" + offset +
               '}';
    }
}
