package com.acme.service.impl;

import com.acme.model.Category;
import com.acme.model.Node;
import com.acme.service.TreeService;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class TreeServiceImpl implements TreeService {

    List<Node> roots;

    @Override
    public List<Node> generateCategoryTree(List<Category> list){
        roots = new ArrayList<>();
        lookUp(list);
        return roots;
    }

    private void lookUp(List<Category> list){

        List<Category> nodes = Lists.newArrayList(list);
        List<Category> categoryRoots = Lists.newArrayList();
        //собираем родителей
        for(Category category : nodes){
            if(Strings.isNullOrEmpty(category.getParentId())){
                categoryRoots.add(category);
            }
        }
        //удаляем корни из списка
        nodes.removeAll(categoryRoots);

        //конвертируем в nodes
        roots = Lists.newArrayList(Lists.transform(categoryRoots, TreeServiceImpl.this::category2Node));

        //Итерируемся по оставшимся
        while(nodes.size()>0){
            for (Iterator<Category> iterator = nodes.iterator(); iterator.hasNext();) {
                Category category = iterator.next();

                //ищем родителя в корнях
                for(Node node: roots) {
                    boolean res = findNode(node, category);
                    if (res) {
                        iterator.remove();
                    }
                }
            }
//            System.out.println("Left in list -> "+ nodes.size());
        }
    }

    private boolean findNode(Node node, Category category){
        boolean res = false;
        if(node!=null){
            if(!node.getId().contentEquals(category.getParentId())){
                if(node.haveChildren()){
                    for(Node child : node.getNodes()){
                        res = findNode(child,category);
                        if(res){
                            break;
                        }
                    }
                }
            } else {
                node.getNodes().add(category2Node(category));
                node.setNoLeaf(true);
                res = true;
            }
        }
        return res;
    }

    public Node category2Node(Category category){
        return new Node(category.getId(),category.getName(),category.getParentId());
    }
}
