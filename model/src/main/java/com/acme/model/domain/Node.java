package com.acme.model.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
    String id;
    String title;
    List<Node> nodes;

    public Node(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public Node() {
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

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public boolean haveChildren(){
        return (nodes!=null && nodes.size()>0);
    }
}