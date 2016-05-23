package com.acme.model.dto;

import com.acme.model.Item;
import com.google.common.collect.Lists;

import java.util.List;

public class ItemMediaTransfer extends Item{

    List<String> media = Lists.newArrayList();

    public List<String> getMedia() {
        return media;
    }

    public void setMedia(List<String> media) {
        this.media = media;
    }

    @Override
    public String toString() {
        return "ItemMediaTransfer{" +
                "media=" + media +
                "} " + super.toString();
    }
}
