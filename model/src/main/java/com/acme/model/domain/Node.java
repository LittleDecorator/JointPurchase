package com.acme.model.domain;

import com.acme.gen.domain.Item;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
    String id;
    String title;
    String parentId;
    List<Item> items;
    List<Node> nodes;
    boolean isCompany = Boolean.FALSE;

    public Node(String id, String title, List<Item> items,String parentId) {
        this.id = id;
        this.title = title;
        this.items = Lists.newArrayList(items);
        this.parentId = parentId;
    }

    public Node() {}

    public boolean isCompany() {
        return isCompany;
    }

    public void setIsCompany(boolean isCompany) {
        this.isCompany = isCompany;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Node> getNodes() {
        if(nodes==null){
            nodes = new ArrayList<>();
        }
        return nodes;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public boolean haveChildren(){
        return (nodes!=null && nodes.size()>0);
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", parentId='" + parentId + '\'' +
                ", items=" + items +
                ", nodes=" + nodes +
                ", isCompany=" + isCompany +
                '}';
    }
}