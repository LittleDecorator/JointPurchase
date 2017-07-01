package com.acme.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by nikolay on 26.06.17.
 *
 * Сущность пользователя Instagram
 */
@Entity
@Table(name = "instagram_users")
public class InstagramUser implements BaseModel {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "origin_id")
    private String originId;

    @Column(name = "subject_id")
    private String subjectId;

    @Column(name = "name")
    private String username;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "profile_image_id")
    private String profilePictureId;

    @Column(name = "description")
    private String bio;

    @Column(name = "website")
    private String website;

    @Column(name = "followers_count")
    private int follows;

    @Column(name = "followed_by_count")
    private int followedBy;

    @Column(name = "date_add", nullable = false, updatable = false)
    private Date dateAdd = new Date();

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

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePictureId() {
        return profilePictureId;
    }

    public void setProfilePictureId(String profilePictureId) {
        this.profilePictureId = profilePictureId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public int getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(int followedBy) {
        this.followedBy = followedBy;
    }

    public Date getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(Date dateAdd) {
        this.dateAdd = dateAdd;
    }

    @Override
    public String toString() {
        return "InstagramUser{" +
                "id='" + id + '\'' +
                ", originId='" + originId + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", profilePictureId='" + profilePictureId + '\'' +
                ", bio='" + bio + '\'' +
                ", website='" + website + '\'' +
                ", follows=" + follows +
                ", followedBy=" + followedBy +
                ", dateAdd=" + dateAdd +
                '}';
    }
}
