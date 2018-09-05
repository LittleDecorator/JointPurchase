package com.acme.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Set;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.cache.annotation.CacheConfig;

/**
 * Created by nikolay on 11.12.17.
 */

@Entity
@Table(name = "sale")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@CacheConfig(cacheNames = "sale")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
public class Sale {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;

    @Column(name = "banner_id")
    private String bannerId;

    @Column(name = "discount")
    private int discount;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @OneToMany(mappedBy = "sale")
    //@JoinTable(name="sale_item",
    //        joinColumns={@JoinColumn(name="sale_id", referencedColumnName="id")},
    //        inverseJoinColumns={@JoinColumn(name="item_id", referencedColumnName="id")})
    @JsonBackReference
    @JsonIgnore
    private Set<Item> items;

    private boolean active;

    @Column(name = "translite_name")
    private String transliteName;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd;

    @PrePersist
    public void prepare(){
        this.setDateAdd(new Date());
    }
}
