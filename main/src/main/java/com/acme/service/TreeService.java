package com.acme.service;

import com.acme.helper.CategoryTypeLink;
import com.acme.model.domain.Node;
import com.google.common.base.Strings;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class TreeService {

    Node tree;
    List<Node> roots;

    public Node generateCategoryTree(List<CategoryTypeLink> list){
        roots = new ArrayList<>();
        lookUp(list);
        if(roots.size()==1){
            tree = roots.get(0);
        } else {
            tree = new Node();
            tree.getNodes().addAll(roots);
        }
        return tree;
    }

    private void lookUp(List<CategoryTypeLink> list){
        List<CategoryTypeLink> nodes = new ArrayList<>(list);
        for (Iterator<CategoryTypeLink> iterator = nodes.iterator(); iterator.hasNext();) {
            CategoryTypeLink categoryTypeLink = iterator.next();
            //если попался корень
            if(Strings.isNullOrEmpty(categoryTypeLink.getParentId())){
                roots.add(category2Node(categoryTypeLink));
                iterator.remove();
            } else {
                //ищем родителя в корнях
                for(Node node: roots){
                    Node res = findNode(node,categoryTypeLink.getParentId());
                    if(res!=null){
                        res.getNodes().add(category2Node(categoryTypeLink));
                        iterator.remove();
                    }
                }
            }
        }
        if(nodes.size()>0){
            lookUp(nodes);
        }
    }

    private Node category2Node(CategoryTypeLink category){
        return new Node(category.getId(),category.getName(),category.getTypes());
    }

/*
    private List<Node> category2Node(List<Category> list){
        List<Node> nodes = new ArrayList<>();
        for(Category c : list){
            nodes.add(new Node(c.getId(),c.getName()));
        }
        return nodes;
    }
*/

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
