package com.acme.model;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {

    private String id;
    private String name;
    private int level;
    private String title;
    private boolean noLeaf = false;
    private int patternUid;
    private String parentId;
    private List<Item> items;
    private List<Node> nodes;
    private boolean isCompany = Boolean.FALSE;
    private String description;

    public Node() {}

    public Node(String id, String name, String title) {
        this.id = id;
        this.title = title;
        this.name = name;
    }

    public Node(String id, String name, String title, String parentId) {
        this(id, name, title);
        this.items = Lists.newArrayList();
        this.parentId = parentId;
    }

    public Node(String id, String name, String title, String parentId, String description) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.parentId = parentId;
        this.description = description;
    }

    public void setCompany(boolean company) {
        isCompany = company;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isNoLeaf() {
        return noLeaf;
    }

    public void setNoLeaf(boolean noLeaf) {
        this.noLeaf = noLeaf;
    }

    public int getPatternUid() {
        return patternUid;
    }

    public void setPatternUid(int patternUid) {
        this.patternUid = patternUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}