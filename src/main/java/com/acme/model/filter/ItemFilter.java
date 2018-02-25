package com.acme.model.filter;

import com.acme.model.Company;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemFilter {

    private String name;
    private String article;
    private Company company;
    private Integer limit;
    private Integer offset;

}
