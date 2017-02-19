package com.acme.model;

import com.acme.model.embedded.ItemContentId;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "item_content")
public class ItemContent {

//    @EmbeddedId
//    private ItemContentId id = new ItemContentId();
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;

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
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
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

}