package com.acme.model;


import java.math.BigDecimal;
import java.util.Date;

public class PurchaseOrder extends Base {

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


    private String status;


    private String delivery;


    private BigDecimal payment;


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }


    public String getSubjectId() {
        return subjectId;
    }


    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId == null ? null : subjectId.trim();
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
        this.recipientFname = recipientFname == null ? null : recipientFname.trim();
    }


    public String getRecipientLname() {
        return recipientLname;
    }


    public void setRecipientLname(String recipientLname) {
        this.recipientLname = recipientLname == null ? null : recipientLname.trim();
    }


    public String getRecipientMname() {
        return recipientMname;
    }

    public void setRecipientMname(String recipientMname) {
        this.recipientMname = recipientMname == null ? null : recipientMname.trim();
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail == null ? null : recipientEmail.trim();
    }


    public String getRecipientPhone() {
        return recipientPhone;
    }


    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone == null ? null : recipientPhone.trim();
    }


    public String getRecipientAddress() {
        return recipientAddress;
    }


    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress == null ? null : recipientAddress.trim();
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
        this.comment = comment == null ? null : comment.trim();
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }


    public String getDelivery() {
        return delivery;
    }


    public void setDelivery(String delivery) {
        this.delivery = delivery == null ? null : delivery.trim();
    }


    public BigDecimal getPayment() {
        return payment;
    }


    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "id='" + id + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", uid=" + uid +
                ", recipientFname='" + recipientFname + '\'' +
                ", recipientLname='" + recipientLname + '\'' +
                ", recipientMname='" + recipientMname + '\'' +
                ", recipientEmail='" + recipientEmail + '\'' +
                ", recipientPhone='" + recipientPhone + '\'' +
                ", recipientAddress='" + recipientAddress + '\'' +
                ", dateAdd=" + dateAdd +
                ", closeOrderDate=" + closeOrderDate +
                ", comment='" + comment + '\'' +
                ", status='" + status + '\'' +
                ", delivery='" + delivery + '\'' +
                ", payment=" + payment +
                '}';
    }
}