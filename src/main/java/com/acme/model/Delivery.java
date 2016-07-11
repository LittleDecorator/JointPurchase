package com.acme.model;

import java.util.Date;

public class Delivery {

    String id;
    String name;
    String hint;
    Date dateAdd;

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

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", hint='" + hint + '\'' +
                ", dateAdd=" + dateAdd +
                '}';
    }
}
