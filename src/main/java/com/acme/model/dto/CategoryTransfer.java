package com.acme.model.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryTransfer {

    private String name;
    private String id;
    private String parentId;
    private List<String> items;

}
