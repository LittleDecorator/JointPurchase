package com.acme.model.domain;

import com.acme.gen.domain.Type;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
    String id;
    String title;
    String parentId;
    List<Type> types;
    List<Node> nodes;
    boolean isCompany = Boolean.FALSE;

    public Node(String id, String title, List<Type> types,String parentId) {
        this.id = id;
        this.title = title;
        this.types = Lists.newArrayList(types);
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

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
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
                ", types=" + types +
                '}';
    }
}