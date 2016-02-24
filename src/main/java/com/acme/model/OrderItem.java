package com.acme.model;

import java.util.Date;

public class OrderItem {

    private String id;


    private String orderId;


    private String itemId;


    private Integer cou;


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