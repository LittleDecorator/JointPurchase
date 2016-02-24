package com.acme.model;


import java.util.Date;

public class Role extends Base {

    private String id;


    private String description;


    private String parentRoleId;


    private Date dateAdd;


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

    @Override
    public String toString() {
        return "Role{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", parentRoleId='" + parentRoleId + '\'' +
                ", dateAdd=" + dateAdd +
                '}';
    }
}