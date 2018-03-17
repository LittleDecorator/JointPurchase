package com.acme.model.dto;

import com.acme.enums.ItemStatus;
import com.acme.model.Category;
import com.acme.model.Sale;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO для списка товаров
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ItemDto {

    private String id;
    private String name;

    private int price;
    private String article;
    private int inOrder;
    private int inStock;
    private boolean notForSale;

    private String companyId;
    private String companyName;
    private String description;
    private String age;
    private String size;
    private String material;
    private ItemStatus status;
    private List<Category> categories;

    private Sale sale;
    private boolean bestseller;
    private String transliteName;

    public ItemDto(String id, String name, int price, String article) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.article = article;
    }

}
