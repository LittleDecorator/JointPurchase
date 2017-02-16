package com.acme.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "item_content")
public class ItemContent {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @Column(name = "item_id")
    private String itemId;

    @Column(name = "content_id")
    private String contentId;

    @Column(name = "crop_id")
    private String cropId;

    private boolean show;

    private boolean main;

    @Column(name = "date_add")
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

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    @Override
    public String toString() {
        return "ItemContent{" +
                "id='" + id + '\'' +
                ", itemId='" + itemId + '\'' +
                ", contentId='" + contentId + '\'' +
                ", cropId='" + cropId + '\'' +
                ", show=" + show +
                ", main=" + main +
                ", dateAdd=" + dateAdd +
                '}';
    }
}