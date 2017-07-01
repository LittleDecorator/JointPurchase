package com.acme.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by nikolay on 01.07.17.
 */

@Entity
@Table(name = "instagram_post_content")
public class InstagramPostContent implements BaseModel {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "post_id")
    private String postId;

    @Column(name = "content_id")
    private String contentId;

    private boolean show;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd = new Date();

    @Transient
    private String url;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "InstagramPostContent{" +
                "id='" + id + '\'' +
                ", postId='" + postId + '\'' +
                ", contentId='" + contentId + '\'' +
                ", show=" + show +
                ", dateAdd=" + dateAdd +
                ", url='" + url + '\'' +
                '}';
    }
}
