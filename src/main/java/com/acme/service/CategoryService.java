package com.acme.service;

import com.acme.exception.PersistException;
import com.acme.model.Category;
import com.acme.model.CategoryItem;
import com.acme.model.Item;
import com.acme.model.Node;
import com.acme.model.dto.CategoryTransfer;
import com.acme.model.dto.MapDto;
import java.util.Set;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public interface CategoryService {

    List<Category> getAll();

    List<Category> getAllByParentId(String parentId);

    List<Category> getAllByIdIn(List<String> ids);

    Category getCategory(String categoryId);

    Category getCategoryByName(String name);

    Set<Item> getCategoryItems(String categoryId);

    Set<Item> getCategoryItems(List<String> categoryIds);

    Set<Item> getCategoryItemsByName(String name);

    List<Category> getRootCategories(String companyId);

    void createCategory(CategoryTransfer transfer);

    Category createCategoryFromNode(Node node);

    void deleteCategory(String categoryId);

    List<CategoryItem> createCategoryItemList4Category(String categoryId, List<String> itemIdList);

    List<CategoryItem> createCategoryItemList4Item(String itemId, List<String> categoryIdList);

    void persistCategoryTree(JSONObject main) throws IOException, ParseException, PersistException;

    List<Node> getRootNodes();

    List<Node> getCompanyNodes();

    List<MapDto> getChildren(String categoryId);

}
