package com.acme.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import org.hibernate.annotations.GenericGenerator;

/**
 * Created by nikolay on 11.12.17.
 */

@Entity
@Table(name = "sale")
public class Sale {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;

    @Column(name = "banner_id")
    private String bannerId;

    @Column(name = "discount")
    private int discount;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @OneToMany()
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

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
