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
@Table(name = "delivery")
//@CacheConfig(cacheNames = "delivery")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@EqualsAndHashCode
public class Delivery implements BaseModel{

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    private String name;

    private String hint;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd;

    @PrePersist
    public void prePersist(){
        if(this.dateAdd == null){
            this.dateAdd = new Date();
        }
    }

}
