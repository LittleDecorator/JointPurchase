package com.acme.model.dto;

import com.acme.model.Item;

import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by nikolay on 24.09.17.
 */
@Getter
@Setter
@ToString
public class WishlistDto {

    private String email;
    private String subjectId;
    private Set<Item> items;

}
