package com.acme.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "content")
public class Content {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @Column(name = "file_name")
    private String fileName;

    private String mime;

    private String type;

    @Column(name = "is_default")
    private boolean isDefault;

    @Column(name = "date_add")
    private Date dateAdd;

    @Transient
    private byte[] content;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "content", cascade= CascadeType.ALL)
    private List<OrderItem> orderItems;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime == null ? null : mime.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Content{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", mime='" + mime + '\'' +
                ", type='" + type + '\'' +
                ", isDefault=" + isDefault +
                ", dateAdd=" + dateAdd +
                '}';
    }
}