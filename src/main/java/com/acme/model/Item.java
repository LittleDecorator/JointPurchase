package com.acme.model;

import com.acme.enums.ItemStatus;
import com.acme.enums.converters.ItemStatusConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "item")
@Document(indexName = "item-index",type = "item-type")
@Setting(settingPath = "/elastic/item/settings.json")
@Mapping(mappingPath = "/elastic/item/mappings.json")
public class Item {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @Column(name = "name")
    private String name;

    @OneToOne
    @JoinColumn(name="company_id")
    private Company company;

    @Column(name = "article")
    private String article;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "date_add")
    private Date dateAdd;

    @Column(name = "not_for_sale")
    private boolean notForSale;

    @Column(name = "in_stock")
    private Integer inStock;

    @Convert(converter = ItemStatusConverter.class)
    private ItemStatus status = ItemStatus.AVAILABLE;

    @Transient
    private List<Category> categories;

    @Transient
    @JsonProperty("images")
    private List<ItemContent> itemContents;

    @Transient
    private List<OrderItem> orderItems;

    @Transient
    private String url;

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

    public List<ItemContent> getItemContents() {
        return itemContents;
    }

    public void setItemContents(List<ItemContent> itemContents) {
        this.itemContents = itemContents;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}