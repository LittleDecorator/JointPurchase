package com.acme.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @Column(name="order_id")
    private String orderId;

    @Column(name = "item_id")
    private String itemId;

    private Integer cou;

    @Column(name = "date_add")
    private Date dateAdd;


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }


    public String getOrderId() {
        return orderId;
    }


    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }


    public String getItemId() {
        return itemId;
    }


    public void setItemId(String itemId) {
        this.itemId = itemId == null ? null : itemId.trim();
    }


    public Integer getCou() {
        return cou;
    }


    public void setCou(Integer cou) {
        this.cou = cou;
    }


    public Date getDateAdd() {
        return dateAdd;
    }


    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id='" + id + '\'' +
                ", orderId='" + orderId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", cou=" + cou +
                ", dateAdd=" + dateAdd +
                '}';
    }
}