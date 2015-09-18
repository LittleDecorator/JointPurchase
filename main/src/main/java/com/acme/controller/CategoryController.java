package com.acme.controller;

import com.acme.gen.domain.*;
import com.acme.gen.mapper.CategoryMapper;
import com.acme.gen.mapper.CategoryTypeMapper;
import com.acme.gen.mapper.TypeMapper;
import com.acme.helper.CategoryTypeLink;
import com.acme.model.domain.Node;
import com.acme.service.TreeService;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping(value = "/category")
public class CategoryController {

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    TreeService treeService;

    @Autowired
    CategoryTypeMapper categoryTypeMapper;

    @Autowired
    TypeMapper typeMapper;

    @RequestMapping(method = RequestMethod.GET,value = "/map")
    public List<Map<String, String>> getCategoryMap() {
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map;

        for(Category category : categoryMapper.selectByExample(new CategoryExample())){
            map = new HashMap<>();
            map.put("id",category.getId());
            map.put("name",category.getName());
            list.add(map);
        }
        return list;
    }

    @RequestMapping(method = RequestMethod.GET,value = "/tree")
    public Node getCategoryTree(){
        List<CategoryTypeLink> links = new ArrayList<>();
        CategoryTypeLink link;
        List<String> list = Lists.newArrayList();
        //TODO: move code below to custom mapper
        //get categories
        List<Category> categories = categoryMapper.selectByExample(new CategoryExample());
        //get all types
        List<Type> types = typeMapper.selectByExample(new TypeExample());
        //get linked types
        List<CategoryType> categoryTypes = categoryTypeMapper.selectByExample(new CategoryTypeExample());

        //merge lists into fake model
        for(Category category : categories){
            List<Type> typeList = Lists.newArrayList();
            link = new CategoryTypeLink();
            link.setId(category.getId());
            link.setName(category.getName());
            link.setParentId(category.getParentId());

            Iterator<CategoryType> categoryTypeIterator = categoryTypes.iterator();
            while(categoryTypeIterator.hasNext()){
                CategoryType categoryType = categoryTypeIterator.next();
                if(categoryType.getCategoryId().contentEquals(category.getId())){
                    list.add(categoryType.getTypeId());
                }
            }
            if(list.size()>0){
                Iterator<Type> typeIterator = types.iterator();
                while(typeIterator.hasNext()){
                    Type type = typeIterator.next();
                    if(list.contains(type.getId())){
                        typeList.add(type);
                    }
                }
            }

            link.setTypes(typeList);
            links.add(link);
        }
        return treeService.generateCategoryTree(links);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/types")
    public List getAllTypes(){
        return typeMapper.selectByExample(new TypeExample());
    }
}
