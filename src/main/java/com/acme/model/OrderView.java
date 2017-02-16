package com.acme.model;

import com.acme.enums.OrderStatus;
import com.acme.enums.converters.ItemStatusConverter;
import com.acme.enums.converters.OrderStatusConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "order_view")
public class OrderView {
    @Id
    private String id;

    private Long uid;

    @Column(name = "recipient_id")
    private String recipientId;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "create_order_date")
    private Timestamp createDate;

    private String delivery;

    @Convert(converter = OrderStatusConverter.class)
    private OrderStatus status;

    private Integer payment;

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
