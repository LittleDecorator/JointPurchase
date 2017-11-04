package com.acme.model.dto;

import java.util.Objects;

/**
 * Created by nikolay on 13.08.17.
 *
 * Класс предоставляющий данные для списка слиентов
 *
 */
public class MapDto {

    private String id;
    private String name;

    public MapDto(String id, String name) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapDto mapDto = (MapDto) o;
        return Objects.equals(id, mapDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
