package com.acme.model.dto;

import com.acme.enums.ItemStatus;
import com.acme.model.Category;
import com.acme.model.ItemContent;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Detail DTO for item that available to client
 */
@Getter
@Setter
public class CatalogDetailDto {

    private String id;
    private String name;
    private String company;
    private String article;
    private String description;
    private Integer price;
    private Integer salePrice;
    private String age;
    private String size;
    private String material;
    private boolean bestseller;
    private boolean inWishlist;
    private ItemStatus status;

    private List<Category> categories;
    private List<ItemContent> itemContents;
    // main image
    private String url;

}
