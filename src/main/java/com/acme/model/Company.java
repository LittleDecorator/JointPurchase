package com.acme.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    private String name;

    private String description;

    private String address;

    private String email;

    private String phone;

    private String url;

    private String bik;

    private String inn;

    private String ks;

    private String rs;

    @Column(name = "date_add")
    private Date dateAdd;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getBik() {
        return bik;
    }

    public void setBik(String bik) {
        this.bik = bik == null ? null : bik.trim();
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn == null ? null : inn.trim();
    }

    public String getKs() {
        return ks;
    }

    public void setKs(String ks) {
        this.ks = ks == null ? null : ks.trim();
    }

    public String getRs() {
        return rs;
    }

    public void setRs(String rs) {
        this.rs = rs == null ? null : rs.trim();
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", url='" + url + '\'' +
                ", bik='" + bik + '\'' +
                ", inn='" + inn + '\'' +
                ", ks='" + ks + '\'' +
                ", rs='" + rs + '\'' +
                ", dateAdd=" + dateAdd +
                '}';
    }
}