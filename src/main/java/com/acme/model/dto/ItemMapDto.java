package com.acme.model.dto;

import com.acme.enums.ItemStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemMapDto {

    private String id;
    private String article;
    private String name;

    // need for sale page in catalog
    // FIXME need use Sale dto on that page
    private String image;
    private Integer price;
    private Integer salePrice;
    private ItemStatus status;

    ItemMapDto(String id, String article, String name) {
        this.id = id;
        this.name = name;
        this.article = article;
    }
}
