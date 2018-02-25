package com.acme.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by nikolay on 17.10.17.
 */
@Getter
@Setter
public class EmailDto {

    private String subject;
    private String to;
    private String body;

}
