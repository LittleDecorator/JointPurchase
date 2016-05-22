package com.acme.service;

import com.acme.model.CategoryItem;
import com.acme.model.Node;

import java.util.List;

public interface CategoryService {

    List<CategoryItem> createCategoryItemList(String categoryId, List<String> itemIdList);

    List<Node> getRootNodes();
}
