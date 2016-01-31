package com.acme.service;

import com.acme.gen.domain.Category;
import com.acme.model.domain.Node;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
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
        roots = Lists.newArrayList(Lists.transform(categoryRoots, new Function<Category, Node>() {
            @Nullable
            @Override
            public Node apply(Category category) {
                return category2Node(category);
            }
        }));

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
            System.out.println("Left in list -> "+ nodes.size());
        }
    }

    private boolean findNode(Node node, Category category){
        System.out.println(node);
        boolean res = false;
        if(node!=null){
            System.out.println(category.getId());
            System.out.println(category.getParentId());
            System.out.println(category.getName());
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
                res = true;
            }
        }
        return res;
    }

    private Node category2Node(Category category){
        return new Node(category.getId(),category.getName(),category.getParentId());
    }
}
