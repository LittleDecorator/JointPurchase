package com.acme.model.dto;

import com.acme.model.Item;

import java.util.List;

/**
 * Created by nikolay on 24.09.17.
 */
public class WishlistDto {

    private String email;
    private String subjectId;
    private List<Item> items;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "WishlistDto{" +
                "email='" + email + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", items=" + items +
                '}';
    }
}
