package com.acme.model.embedded;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CategoryItemId implements Serializable {

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "item_id")
    private String itemId;

    public CategoryItemId() {
    }

    public CategoryItemId(String categoryId, String itemId) {
        this.categoryId = categoryId;
        this.itemId = itemId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
