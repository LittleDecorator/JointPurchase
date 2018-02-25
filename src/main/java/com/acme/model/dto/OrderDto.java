package com.acme.model.dto;

import com.acme.enums.OrderStatus;
import com.acme.model.Delivery;
import com.acme.model.OrderItem;
import java.util.Date;
import java.util.List;

public class OrderDto {

    private String id;

    private String subjectId;

    private Long uid;

    private String recipientFname;

    private String recipientLname;

    private String recipientMname;

    private String recipientEmail;

    private String recipientPhone;

    private String recipientAddress;

    private Date dateAdd;

    private Date closeOrderDate;

    private String comment;

    private OrderStatus status;

    private Delivery delivery;

    private Integer payment;

    private List<OrderItem> orderItems;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getRecipientFname() {
        return recipientFname;
    }

    public void setRecipientFname(String recipientFname) {
        this.recipientFname = recipientFname;
    }

    public String getRecipientLname() {
        return recipientLname;
    }

    public void setRecipientLname(String recipientLname) {
        this.recipientLname = recipientLname;
    }

    public String getRecipientMname() {
        return recipientMname;
    }

    public void setRecipientMname(String recipientMname) {
        this.recipientMname = recipientMname;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

    public Date getCloseOrderDate() {
        return closeOrderDate;
    }

    public void setCloseOrderDate(Date closeOrderDate) {
        this.closeOrderDate = closeOrderDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public Integer getPayment() {
        return payment;
    }

    public void setPayment(Integer payment) {
        this.payment = payment;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
