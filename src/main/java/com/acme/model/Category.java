package com.acme.model;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import org.springframework.cache.annotation.CacheConfig;

@Entity
@Table(name = "category")
//@CacheConfig(cacheNames = "category")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@EqualsAndHashCode
public class Category implements BaseModel {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    private String name;

    @Column(name = "translite_name")
    private String transliteName;

    @Column(name = "description")
    private String description;

    @Column(name = "content_id")
    private String contentId;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd;

    @Transient
    private Set<Item> items;

    @PrePersist
    public void prePersist(){
        if(this.dateAdd == null){
            this.dateAdd = new Date();
        }
    }
}