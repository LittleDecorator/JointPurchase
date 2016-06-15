package com.acme.model.dto;

import com.acme.model.Item;

public class Product extends Item {

    String contentId;
    String companyName;
    String imageUrl;

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public String toString() {
        return "Product{" +
                "contentId='" + contentId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                "} " + super.toString();
    }
}
