package com.acme.model;

import com.acme.enums.ItemStatus;
import com.acme.enums.converters.ItemStatusConverter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @NotNull
    private String name;

    @OneToOne
    private Company company;

    @NotNull
    private String article;

    @NotNull
    private String description;

    @NotNull
    private Integer price;

    @Column(name = "date_add")
    private Date dateAdd = new Date();

    @Column(name = "not_for_sale")
    private boolean notForSale = true;

    @Column(name = "in_stock")
    private Integer inStock;

    @OneToMany
    private List<Category> categories;

    @Convert(converter = ItemStatusConverter.class)
    private ItemStatus status = ItemStatus.AVAILABLE;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.item")
    private List<OrderItem> orderItems;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article == null ? null : article.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

    public boolean isNotForSale() {
        return notForSale;
    }

    public void setNotForSale(boolean notForSale) {
        this.notForSale = notForSale;
    }

    public Integer getInStock() {
        return inStock;
    }

    public void setInStock(Integer inStock) {
        this.inStock = inStock;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "Item{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", company=" + company +
               ", article='" + article + '\'' +
               ", description='" + description + '\'' +
               ", price=" + price +
               ", dateAdd=" + dateAdd +
               ", notForSale=" + notForSale +
               ", inStock=" + inStock +
               ", categories=" + categories +
               ", status=" + status +
               '}';
    }
}