package com.acme.model.embedded;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter
@Getter
@EqualsAndHashCode
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


}
