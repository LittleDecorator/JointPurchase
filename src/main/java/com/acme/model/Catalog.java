package com.acme.model;

import com.acme.enums.ItemStatus;
import com.acme.enums.converters.ItemStatusConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "catalog")
public class Catalog implements Product {

    @Id
    private String id;

    @Column(name = "name", updatable = false)
    private String name;

    @Column(name = "translite_name", updatable = false)
    private String transliteName;

    @OneToOne
    @JoinColumn(name="company_id", updatable = false)
    private Company company;

    @Column(name = "article", updatable = false)
    private String article;

    @Column(name = "description", updatable = false)
    private String description;

    @Column(name = "price", updatable = false)
    private Integer price;

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

    @OneToOne
    @JoinColumn(name="category_id", updatable = false)
    private Category category;

    @OneToOne
    @JoinColumn(name="parent_category_id", updatable = false)
    private Category parentCategory;

    @Transient
    @JsonProperty("images")
    private List<ItemContent> itemContents;

    @Transient
    private String url;

    @Override
    public void setCategories(List<Category> categories) {
        // do nothing
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransliteName() {
        return transliteName;
    }

    public void setTransliteName(String transliteName) {
        this.transliteName = transliteName;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
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

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<ItemContent> getItemContents() {
        return itemContents;
    }

    public void setItemContents(List<ItemContent> itemContents) {
        this.itemContents = itemContents;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isInWishlist() {
        return inWishlist;
    }

    public void setInWishlist(boolean inWishlist) {
        this.inWishlist = inWishlist;
    }
}
