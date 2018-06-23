package com.acme.model;

import javax.persistence.PrePersist;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import org.springframework.cache.annotation.CacheConfig;

@Entity
@Table(name = "company")
//@CacheConfig(cacheNames = "company")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@EqualsAndHashCode
public class Company implements BaseModel{

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    private String name;

    @Column(name = "translite_name")
    private String transliteName;

    @Column(name = "short_description")
    private String shortDescription;

    private String description;

    private String address;

    private String email;

    private String phone;

    private String url;

    private String bik;

    private String inn;

    private String ks;

    private String rs;

    @Column(name = "content_id")
    private String contentId;

    @Column(name = "date_add", insertable = false, updatable = false)
    private Date dateAdd;
}