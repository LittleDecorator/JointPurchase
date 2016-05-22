package com.acme.model.dto;

import java.util.List;

public class CategoryTransfer {

    String name;
    String id;
    String parentId;
    List<String> items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CategoryTransfer{" +
                "name='" + name + '\'' +
                ", parentId='" + parentId + '\'' +
                ", items=" + items +
                '}';
    }
}
