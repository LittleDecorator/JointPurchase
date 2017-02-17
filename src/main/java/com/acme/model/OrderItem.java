package com.acme.model;

import com.acme.model.embedded.OrderItemId;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "order_item")
public class OrderItem {

    @EmbeddedId
    private OrderItemId id = new OrderItemId();

    @ManyToOne
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private PurchaseOrder order;

    @ManyToOne
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private Item item;

    private Integer count;

    @Column(name = "date_add")
    private Date dateAdd;

    public OrderItem(PurchaseOrder order, Item item, Integer count) {
        // create primary key
        this.id = new OrderItemId(order.getId(), item.getId());

        // initialize attributes
        this.order = order;
        this.item = item;
        this.count = count;

        // update relationships to assure referential integrity
        item.getOrderItems().add(this);
        order.getOrderItems().add(this);
    }

    public OrderItemId getId() {
        return id;
    }

    public void setId(OrderItemId id) {
        this.id = id;
    }

    public PurchaseOrder getOrder() {
        return order;
    }

    public void setOrder(PurchaseOrder order) {
        this.order = order;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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
               ", order=" + order +
               ", item=" + item +
               ", count=" + count +
               ", dateAdd=" + dateAdd +
               '}';
    }
}