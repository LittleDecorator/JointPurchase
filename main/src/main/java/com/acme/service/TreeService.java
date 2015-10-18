package com.acme.service;

import com.acme.gen.domain.Category;
import com.acme.model.domain.Node;
import com.google.common.base.Strings;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class TreeService {

    List<Node> roots;

    public List<Node> generateCategoryTree(List<Category> list){
        roots = new ArrayList<>();
        lookUp(list);
        return roots;
    }

    private void lookUp(List<Category> list){
        List<Category> nodes = new ArrayList<>(list);
        for (Iterator<Category> iterator = nodes.iterator(); iterator.hasNext();) {
            Category category = iterator.next();
            //если попался корень
            if(Strings.isNullOrEmpty(category.getParentId())){
                roots.add(category2Node(category));
                iterator.remove();
            } else {
                //ищем родителя в корнях
                for(Node node: roots){
                    Node res = findNode(node, category.getParentId());
                    if(res!=null){
                        res.getNodes().add(category2Node(category));
                        iterator.remove();
                    }
                }
            }
        }
        if(nodes.size()>0){
            lookUp(nodes);
        }
    }

    private Node category2Node(Category category){
        return new Node(category.getId(),category.getName(),category.getParentId());
    }

    private Node findNode(Node node, String id){
        Node res = null;
        if(!node.getId().contentEquals(id)){
            if(node.haveChildren()){
                for(Node child : node.getNodes()){
                    res = findNode(child,id);
                    if(res!=null) break;
                }
            }
        } else {
            res = node;
        }
        return res;
    }

}
