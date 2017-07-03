package com.acme.model;

import com.google.common.collect.Lists;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by nikolay on 01.07.17.
 *
 * Сущность публикации в instagram
 */

@Entity
@Table(name = "instagram_posts")
public class InstagramPost implements BaseModel {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "origin_id")
    private String originId;

    @Column(name = "content")
    private String content;

    @Column(name = "user_id")
    private String instagramUserId;

    @Column(name = "external_url")
    private String externalUrl;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "user_has_liked")
    private Boolean userHasLiked;

    @Column(name = "likes_count")
    private Integer likesCount;

    @Column(name = "tags")
    private String tags;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd = new Date();

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInstagramUserId() {
        return instagramUserId;
    }

    public void setInstagramUserId(String instagramUserId) {
        this.instagramUserId = instagramUserId;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = new Date(createTime);
    }

    public Boolean getUserHasLiked() {
        return userHasLiked;
    }

    public void setUserHasLiked(Boolean userHasLiked) {
        this.userHasLiked = userHasLiked;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public List<String> getTags() {
        return Lists.newArrayList(tags.split(";"));
    }

    public void setTags(List<String> tags) {
        this.tags = String.join(";",tags);
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

    @Override
    public String toString() {
        return "InstagramPost{" +
                "id='" + id + '\'' +
                ", originId='" + originId + '\'' +
                ", content='" + content + '\'' +
                ", instagramUserId='" + instagramUserId + '\'' +
                ", externalUrl='" + externalUrl + '\'' +
                ", createTime=" + createTime +
                ", userHasLiked=" + userHasLiked +
                ", likesCount=" + likesCount +
                ", tags=" + tags +
                ", dateAdd=" + dateAdd +
                '}';
    }
}
