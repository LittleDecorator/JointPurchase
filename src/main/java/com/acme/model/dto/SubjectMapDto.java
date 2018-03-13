package com.acme.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс предоставляющий данные для списка слиентов
 */

@Getter
@Setter
@NoArgsConstructor
public class SubjectMapDto {

    private String id;
    private String name;

    SubjectMapDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

}
