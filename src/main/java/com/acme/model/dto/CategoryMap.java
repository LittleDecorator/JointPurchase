package com.acme.model.dto;

/**
 * Created by nikolay on 13.08.17.
 *
 * Класс предоставляющий данные для списка слиентов
 *
 */
public class CategoryMap {

    private String id;
    private String name;

    public CategoryMap(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
