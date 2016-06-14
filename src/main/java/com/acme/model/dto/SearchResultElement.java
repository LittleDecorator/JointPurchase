package com.acme.model.dto;

import java.util.List;

public class SearchResultElement {

    String groupId;
    String groupName;
    List<Product> children;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Product> getChildren() {
        return children;
    }

    public void setChildren(List<Product> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "SearchResultElement{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", children=" + children +
                '}';
    }
}
