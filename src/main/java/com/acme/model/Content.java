package com.acme.model;

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
@Table(name = "content")
//@CacheConfig(cacheNames = "content")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@EqualsAndHashCode
public class Content implements BaseModel{

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "file_name")
    private String fileName;

    private String mime;

    private String type;

    @Column(name = "is_default")
    private boolean isDefault;

    @Column(name = "is_instagram")
    private boolean isInstagram;

    @Column(name = "is_profile")
    private boolean isProfile;

    @Column(name = "meta_info")
    //@Enumerated(EnumType.STRING)
    private String metaInfo;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd;

    @Transient
    private String content;

    @PrePersist
    public void prePersist(){
        if(this.dateAdd == null){
            this.dateAdd = new Date();
        }
    }

}