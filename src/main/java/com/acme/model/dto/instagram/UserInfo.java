package com.acme.model.dto.instagram;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nikolay on 26.06.17.
 */
public class UserInfo {

    private String id;
    @JsonProperty(value="profile_picture")
    private String profilePicture;
    private String username;
    @JsonProperty(value="full_name")
    private String fullName;
    private String bio;
    private String website;
    private Metrica counts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public Metrica getCounts() {
        return counts;
    }

    public void setCounts(Metrica counts) {
        this.counts = counts;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", bio='" + bio + '\'' +
                ", website='" + website + '\'' +
                ", counts=" + counts +
                '}';
    }

    /*================ INNERS =================*/

    public class Metrica {
        private int media;
        private int follows;
        @JsonProperty(value="followed_by")
        private int followedBy;

        public Metrica() {
        }

        public Metrica(int media, int follows, int followedBy) {
            this.media = media;
            this.follows = follows;
            this.followedBy = followedBy;
        }

        public int getMedia() {
            return media;
        }

        public void setMedia(int media) {
            this.media = media;
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

        @Override
        public String toString() {
            return "Metrica{" +
                    "media=" + media +
                    ", follows=" + follows +
                    ", followedBy=" + followedBy +
                    '}';
        }
    }
}
