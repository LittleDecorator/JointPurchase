package com.acme.model.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleDto {

    private String id;
    private String title;
    private String description;
    private String bannerId;
    private int discount;
    private Date startDate;
    private Date endDate;
    private Set<ItemMapDto> items;
    private boolean active;
    private String transliteName;

}
