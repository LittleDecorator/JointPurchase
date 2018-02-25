package com.acme.model.dto;

import com.acme.enums.ItemStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * Simplified version of item that available to client
 */

@Getter
@Setter
public class CatalogDto {

    private String id;
    private String name;
    private Integer price;
    private Integer salePrice;
    private ItemStatus status;
    private String transliteName;
    private String url;

}
