package com.acme.model;

import com.acme.enums.ItemStatus;
import com.acme.enums.converters.ItemStatusConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.Date;
import java.util.List;

/**
 * Entity represent Item
 */

@Entity
@Table(name = "item")
@Document(indexName = "item-index", type = "item-type")
@Setting(settingPath = "/elastic/item/settings.json")
@Mapping(mappingPath = "/elastic/item/mappings.json")
//@CacheConfig(cacheNames = "item")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@ToString
public class Item implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "translite_name")
    private String transliteName;

    @OneToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "article")
    private String article;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd;

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
        joinColumns = {@JoinColumn(name = "item_id", referencedColumnName = "id",updatable = false)},
        inverseJoinColumns = {@JoinColumn(name = "sale_id", referencedColumnName = "id", updatable = false)})
    @JsonBackReference
    private Sale sale;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "category_item",
        joinColumns = {@JoinColumn(name = "item_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "category_id", referencedColumnName = "id")})
    private List<Category> categories;

    @JsonProperty("images")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "itemId")
    private List<ItemContent> itemContents;

    @PrePersist
    public void prePersist(){
        if(this.dateAdd == null){
            this.dateAdd = new Date();
        }
    }

}