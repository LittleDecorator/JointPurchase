package com.acme.model;

import java.util.Arrays;
import java.util.Date;

public class Content {

    private String id;
    private String fileName;
    private String mime;
    private String type;
    private boolean isDefault;

    private Date dateAdd;

    private byte[] content;

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