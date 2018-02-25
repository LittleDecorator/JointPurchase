package com.acme.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO для списка товаров
 */
@Getter
@Setter
public class ItemDto {

    private String id;
    private String name;
    private String company;
    private double price;
    private String article;
    private int inOrder;
    private int inStock;
    private boolean notForSale;

}
