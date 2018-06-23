package com.acme.model.filter;

import com.acme.model.Company;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ItemFilter{

    private String name;
    private String article;
    private Company company;

}
