package com.acme.model;

import com.acme.enums.ItemStatus;
import com.acme.enums.converters.ItemStatusConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
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
public class Item implements Product, Serializable{

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "translite_name")
    private String transliteName;

    @OneToOne
    @JoinColumn(name="company_id")
    private Company company;

    @Column(name = "article")
    private String article;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd = new Date();

    @Column(name = "not_for_sale")
    private boolean notForSale;

    @Column(name = "in_stock")
    private Integer inStock;

    @Column(name = "in_order")
    private Integer inOrder;

    @Column(name = "age")
    private String age;

    @Column(name = "size")
    private String size;

    @Column(name = "material")
    private String material;

    private boolean bestseller;

    @Transient
    private boolean inWishlist = false;

    @Convert(converter = ItemStatusConverter.class)
    private ItemStatus status = ItemStatus.AVAILABLE;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "sale_item",
        joinColumns={@JoinColumn(name="item_id", referencedColumnName="id")},
        inverseJoinColumns={@JoinColumn(name="sale_id", referencedColumnName="id")})
    @JsonBackReference
    private Sale sale;

    @Transient
    private Integer salePrice;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name="category_item",
        joinColumns={@JoinColumn(name="item_id", referencedColumnName="id")},
        inverseJoinColumns={@JoinColumn(name="category_id", referencedColumnName="id")})
    private List<Category> categories;

    @JsonProperty("images")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "itemId")
    private List<ItemContent> itemContents;

    //@OneToMany(fetch = FetchType.LAZY, mappedBy = "itemId")
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

    public Integer getInOrder() {
        return inOrder;
    }

    public void setInOrder(Integer inOrder) {
        this.inOrder = inOrder;
    }

    public boolean isInWishlist() {
        return inWishlist;
    }

    public void setInWishlist(boolean inWishlist) {
        this.inWishlist = inWishlist;
    }

    public String getTransliteName() {
        return transliteName;
    }

    public void setTransliteName(String transliteName) {
        this.transliteName = transliteName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public boolean isBestseller() {
        return bestseller;
    }

    public void setBestseller(boolean bestseller) {
        this.bestseller = bestseller;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public Integer getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Integer salePrice) {
        this.salePrice = salePrice;
    }
}