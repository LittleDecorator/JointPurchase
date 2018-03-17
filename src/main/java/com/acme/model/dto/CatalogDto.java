package com.acme.model.dto;

import com.acme.enums.ItemStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Simplified version of item that available to client
 */

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CatalogDto {

    private String id;
    private String name;
    private Integer price;
    private Integer salePrice;
    private ItemStatus status;
    private String companyName;
    private String transliteName;
    private String url;

}
