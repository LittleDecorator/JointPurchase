package com.acme.model.dto;

import java.util.List;

/**
 * Created by nikolay on 01.07.17.
 *
 * Сущьность представляющая публикацию в instagram
 */
public class InstagramPostDto {

    private String originId;
    /* автор */
    private String userId;
    /* ссылка в инстаграм */
    private String externalUrl;
    /* время создания */
    private long createdTime;
    /* заголовок */
    private String caption;
    /**/
    private boolean userHasLiked;
    /* кол-во отметок 'нравиться' */
    private int likesCount;
    /* тэги */
    private List<String> tags;
    /* список изображений */
    private List<String> contentUrls;

    public InstagramPostDto() {
    }

    public InstagramPostDto(String originId, String userId, String externalUrl, long createdTime, String caption,
                            boolean userHasLiked, int likesCount, List<String> tags, List<String> contentUrls) {
        this.originId = originId;
        this.userId = userId;
        this.externalUrl = externalUrl;
        this.createdTime = createdTime;
        this.caption = caption;
        this.userHasLiked = userHasLiked;
        this.likesCount = likesCount;
        this.tags = tags;
        this.contentUrls = contentUrls;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isUserHasLiked() {
        return userHasLiked;
    }

    public void setUserHasLiked(boolean userHasLiked) {
        this.userHasLiked = userHasLiked;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getContentUrls() {
        return contentUrls;
    }

    public void setContentUrls(List<String> contentUrls) {
        this.contentUrls = contentUrls;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    @Override
    public String toString() {
        return "InstagramPost{" +
                "originId='" + originId + '\'' +
                ", userId='" + userId + '\'' +
                ", externalUrl='" + externalUrl + '\'' +
                ", createdTime=" + createdTime +
                ", caption='" + caption + '\'' +
                ", userHasLiked=" + userHasLiked +
                ", likesCount=" + likesCount +
                ", tags=" + tags +
                ", contentUrls=" + contentUrls +
                '}';
    }
}
