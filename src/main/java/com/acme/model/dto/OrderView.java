package com.acme.model.dto;


import com.acme.enums.OrderStatus;

import java.sql.Timestamp;

public class OrderView {

    String id;
    Long uid;
    String recipientId;
    String recipientName;
    Timestamp createDate;
    String delivery;
    OrderStatus status;
    Integer payment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Integer getPayment() {
        return payment;
    }

    public void setPayment(Integer payment) {
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "OrderView{" +
                "id='" + id + '\'' +
                ", uid=" + uid +
                ", recipientId='" + recipientId + '\'' +
                ", recipientName='" + recipientName + '\'' +
                ", createDate=" + createDate +
                ", delivery='" + delivery + '\'' +
                ", status=" + status +
                ", payment=" + payment +
                '}';
    }
}
