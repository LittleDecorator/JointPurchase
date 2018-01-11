package com.acme.model;

import com.acme.model.embedded.CategoryItemId;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "category_item")
public class CategoryItem {

    //@Id
    //@GeneratedValue(generator = "system-uuid")
    //@GenericGenerator(name = "system-uuid", strategy = "uuid2")
    //private String id;

    @EmbeddedId
    private CategoryItemId id;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd = new Date();

    public CategoryItemId getId() {
        return id;
    }

    public void setId(CategoryItemId id) {
        this.id = id;
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

}