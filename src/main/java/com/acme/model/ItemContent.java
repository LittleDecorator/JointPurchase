package com.acme.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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

@Entity
@Table(name = "item_content")
//@CacheConfig(cacheNames = "item_content")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@EqualsAndHashCode
public class ItemContent implements BaseModel {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "item_id")
    private String itemId;

    @OneToOne
    @JoinColumn(name = "content_id")
    private Content content;

    @Column(name = "crop_id")
    private String cropId;

    private boolean show;

    private boolean main;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd;

    @Transient
    private String url;

    @PrePersist
    public void prePersist(){
        if(this.dateAdd == null){
            this.dateAdd = new Date();
        }
    }
}