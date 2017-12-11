package com.acme.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by nikolay on 11.12.17.
 */

@Entity
@Table(name = "sale")
public class Sale {

    @Id
    private String id;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;

    @OneToOne
    private Content banner_id;

    @Column(name = "discount")
    private int discount;

    @OneToMany
    @JoinTable(name="sale_item",
            joinColumns={@JoinColumn(name="sale_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="item_id", referencedColumnName="id")})
    private List<Item> items;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd = new Date();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Content getBanner_id() {
        return banner_id;
    }

    public void setBanner_id(Content banner_id) {
        this.banner_id = banner_id;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }
}
