package com.acme.model;

import java.util.Date;

public class ItemContent {
    private String id;
    private String itemId;
    private String contentId;
    private boolean show;
    private boolean main;
    private Date dateAdd;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }
    public String getItemId() {
        return itemId;
    }
    public void setItemId(String itemId) {
        this.itemId = itemId == null ? null : itemId.trim();
    }
    public String getContentId() {
        return contentId;
    }
    public void setContentId(String contentId) {
        this.contentId = contentId == null ? null : contentId.trim();
    }
    public boolean isShow() {
        return show;
    }
    public void setShow(boolean show) {
        this.show = show;
    }
    public boolean isMain() {
        return main;
    }
    public void setMain(boolean main) {
        this.main = main;
    }
    public Date getDateAdd() {
        return dateAdd;
    }
    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

    @Override
    public String toString() {
        return "ItemContent{" +
                "id='" + id + '\'' +
                ", itemId='" + itemId + '\'' +
                ", contentId='" + contentId + '\'' +
                ", show=" + show +
                ", main=" + main +
                ", dateAdd=" + dateAdd +
                '}';
    }
}