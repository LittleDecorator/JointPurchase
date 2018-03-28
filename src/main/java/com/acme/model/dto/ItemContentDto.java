package com.acme.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ItemContentDto {

    private String id;
    private String itemId;
    private String contentId;
    private boolean main;
    private boolean show;
    private String url;

}
