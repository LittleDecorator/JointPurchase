package com.acme.model.dto;

/**
 * Created by nikolay on 24.09.17.
 */
public class WishlistCountDto {

    private String email;
    private String subjectId;
    private int count;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "WishlistDto{" +
                "email='" + email + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", count=" + count +
                '}';
    }
}
