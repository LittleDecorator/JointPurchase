package com.acme.model.dto;

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

    ItemMapDto(String id, String article, String name) {
        this.id = id;
        this.name = name;
        this.article = article;
    }
}
