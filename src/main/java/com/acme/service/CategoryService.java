package com.acme.service;

import com.acme.model.Node;

import java.util.List;

public interface CategoryService {

//    List<CategoryItem> createCategoryItemList4Category(String categoryId, List<String> itemIdList);
//
//    List<CategoryItem> createCategoryItemList4Item(String itemId, List<String> categoryIdList);

    List<Node> getRootNodes();
}
