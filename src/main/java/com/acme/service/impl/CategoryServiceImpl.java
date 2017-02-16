package com.acme.service.impl;

import com.acme.model.Node;
import com.acme.repository.CategoryRepository;
import com.acme.service.CategoryService;
import com.acme.service.TreeService;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    TreeService treeService;

    @Autowired
    CategoryRepository categoryRepository;

//    @Override
//    public List<CategoryItem> createCategoryItemList4Category(String categoryId, List<String> itemIdList) {
//        List<CategoryItem> categoryItems = Lists.newArrayList();
//        categoryItems.addAll(itemIdList.stream().map(itemId -> create(itemId, categoryId)).collect(Collectors.toList()));
//        return categoryItems;
//    }
//
//    @Override
//    public List<CategoryItem> createCategoryItemList4Item(String itemId, List<String> categoryIdList) {
//        List<CategoryItem> categoryItems = Lists.newArrayList();
//        categoryItems.addAll(categoryIdList.stream().map(categoryId -> create(itemId, categoryId)).collect(Collectors.toList()));
//        return categoryItems;
//    }
//
//    private CategoryItem create(String itemId, String categoryId){
//        CategoryItem categoryItem = new CategoryItem();
//        categoryItem.setCategoryId(categoryId);
//        categoryItem.setItemId(itemId);
//        return categoryItem;
//    }

    @Override
    public List<Node> getRootNodes() {
        List<Node> roots = Lists.newArrayList();
        roots.addAll(categoryRepository.getAll().stream().filter(category -> Strings.isNullOrEmpty(category.getParentId())).map(category -> treeService.category2Node(category)).collect(Collectors.toList()));
        return roots;
    }
}
