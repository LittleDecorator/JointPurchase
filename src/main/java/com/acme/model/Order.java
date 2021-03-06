package com.acme.model;

import com.acme.enums.OrderStatus;
import com.acme.enums.converters.OrderStatusConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Entity
@Table(name = "purchase_order")
public class Order implements BaseModel{

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "subject_id")
    private String subjectId;

    private Long uid = System.currentTimeMillis();

    @Column(name = "recipient_fname")
    private String recipientFname;

    @Column(name = "recipient_lname")
    private String recipientLname;

    @Column(name = "recipient_mname")
    private String recipientMname;

    @Column(name = "recipient_email")
    private String recipientEmail;

    @Column(name = "recipient_phone")
    private String recipientPhone;

    @Column(name = "recipient_address")
    private String recipientAddress;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd = new Date();

    @Column(name = "close_order_date")
    private Date closeOrderDate;

    private String comment;

    @Convert(converter = OrderStatusConverter.class)
    private OrderStatus status;

    @JoinColumn(name = "delivery_id", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.LAZY)
    private Delivery delivery;

    private Integer payment;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private List<OrderItem> orderItems;

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