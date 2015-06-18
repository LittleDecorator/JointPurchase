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

    Node tree;
    List<Node> roots;
    int i = 0;

    public Node generateCategoryTree(List<Category> list){
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

    private void lookUp(List<Category> list){
        List<Category> nodes = new ArrayList<>(list);
//        System.out.println("Cou left categories -> "+ nodes.size());
        for (Iterator<Category> iterator = nodes.iterator(); iterator.hasNext();) {
            Category category = iterator.next();
//            System.out.println("Check parentId -> "+category.getParentId());
            //если попался корень
            if(Strings.isNullOrEmpty(category.getParentId())){
//                System.out.println("Find root! -> "+category.getName() + " | "+category.getId());
                roots.add(category2Node(category));
                iterator.remove();
//                System.out.println("Cou left categories -> "+ nodes.size());
            } else {
                //ищем родителя в корнях
                for(Node node: roots){
                    Node res = findNode(node,category.getParentId());
                    if(res!=null){
//                        System.out.println("Find child! -> "+category.getName() + " | "+category.getId());
                        res.getNodes().add(category2Node(category));
//                        nodes.remove(res);
                        iterator.remove();
                    }
                }
            }
        }
        /*if(nodes.size()>0){
            lookUp(nodes);
        }*/
        if(i<5){
            i++;
            lookUp(nodes);
        }
    }

    private Node category2Node(Category category){
        return new Node(category.getId(),category.getName());
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