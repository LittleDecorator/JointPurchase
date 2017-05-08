package com.acme.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "role")
public class Role {

	@Id
    private String id;

    private String description;

	@Column(name = "parent_role_id")
    private String parentRoleId;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd = new Date();


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getParentRoleId() {
        return parentRoleId;
    }

    public void setParentRoleId(String parentRoleId) {
        this.parentRoleId = parentRoleId == null ? null : parentRoleId.trim();
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

}