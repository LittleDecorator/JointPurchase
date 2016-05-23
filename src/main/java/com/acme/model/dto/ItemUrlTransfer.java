package com.acme.model.dto;

import com.acme.model.Item;

public class ItemUrlTransfer extends Item {

    String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ItemUrlTransfer{" +
                "url='" + url + '\'' +
                "} " + super.toString();
    }
}
