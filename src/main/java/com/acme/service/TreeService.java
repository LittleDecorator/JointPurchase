package com.acme.service;

import com.acme.model.Category;
import com.acme.model.Node;

import java.util.List;

public interface TreeService {

    List<Node> generateCategoryTree(List<Category> list);

}
