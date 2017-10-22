package com.acme.model.dto;

/**
 * Created by nikolay on 22.10.17.
 */
public class RouteDto {

    private String name;
    private String url;
    private String parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "RouteDto{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", parent='" + parent + '\'' +
                '}';
    }
}
