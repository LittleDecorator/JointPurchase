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
    private String transliteName;

    public MapDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public MapDto(String id, String name, String transliteName) {
        this.id = id;
        this.name = name;
        this.transliteName = transliteName;
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

    public String getTransliteName() {
        return transliteName;
    }

    public void setTransliteName(String transliteName) {
        this.transliteName = transliteName;
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
