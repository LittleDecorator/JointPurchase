package com.acme.model;


import java.util.Date;

public class Credential {
    private String subjectId;

    private String password;

    private String roleId;

    private Date dateAdd;

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId == null ? null : subjectId.trim();
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId == null ? null : roleId.trim();
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

    @Override
    public String toString() {
        return "Credential{" +
                "subjectId='" + subjectId + '\'' +
                ", password='" + password + '\'' +
                ", roleId='" + roleId + '\'' +
                ", dateAdd=" + dateAdd +
                '}';
    }
}