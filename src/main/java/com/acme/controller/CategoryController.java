package com.acme.controller;

import com.acme.exception.PersistException;
import com.acme.model.*;
import com.acme.model.dto.CategoryMap;
import com.acme.model.dto.CategoryTransfer;
import com.acme.service.CategoryService;
import com.acme.service.TreeService;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/category")
public class CategoryController {

    @Autowired
    TreeService treeService;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    CategoryService categoryService;

    /**
     * Добавление/Обновление новой категории
     *
     * @param transfer
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    public void createCategory(@RequestBody CategoryTransfer transfer) {
        if (transfer != null) {
            TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
            try {
                categoryService.createCategory(transfer);
                transactionManager.commit(status);
            } catch (Exception ex) {
                transactionManager.rollback(status);
            }
        }
    }

    /**
     * Удаление категории по ID
     *
     * @param id
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public void deleteCategory(@PathVariable("id") String id) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            categoryService.deleteCategory(id);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
        }
    }

    /**
     * Получение категории по ID
     *
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public Category getCategory(@PathVariable(value = "id") String id) {
        return categoryService.getCategory(id);
    }

    /**
     * Получение полного списка зависимых категорий
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/children")
    public List<CategoryMap> getChildren(@PathVariable(value = "id") String id) {
        List<CategoryMap> result = Lists.newArrayList(new CategoryMap(id, "Все"));
        result.addAll(categoryService.getChildren(id));
        return result;
    }

    /**
     * Получение мапы категории
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/map")
    public List<CategoryMap> getCategoryMap() {
        List<CategoryMap> result = Lists.newArrayList();
        result.addAll(categoryService.getAll().stream().map(category -> new CategoryMap(category.getId(), category.getName())).collect(Collectors.toList()));
        return result;
    }

    /**
     * Получение корневых узлов дерева категорий в виде мапы
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/map/roots")
    public List<CategoryMap> getCategoryRootMap() {
        List<CategoryMap> result = Lists.newArrayList();
        result.addAll(categoryService.getAll().stream().filter(category -> Strings.isNullOrEmpty(category.getParentId())).map(category -> new CategoryMap(category.getId(), category.getName())).collect(Collectors.toList()));
        return result;
    }

    /**
     * Получение ветки дерева категорий глубиной в 1
     *
     * @param parentId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/sub")
    public List<Category> getSubCategories(@PathVariable(value = "id") String parentId) {
        return categoryService.getAllByParentId(parentId);
    }

    /**
     * Получение дерева категорий
     *
     * @return list of nodes, which just category representation
     */
    @RequestMapping(method = RequestMethod.GET, value = "/tree")
    public List<Node> getCategoryTree() {
        return treeService.generateCategoryTree(categoryService.getAll());
    }

    /**
     * Сохранение дерева категорий
     *
     * @param input
     * @throws ParseException
     * @throws IOException
     */
    //TODO: ADD Items processing in tree
    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/tree")
    public void saveCategoryTree(@RequestBody String input) throws IOException, ParseException, PersistException {
        JSONParser parser = new JSONParser();
        JSONObject main = (JSONObject) parser.parse(input);
        categoryService.persistCategoryTree(main);
    }

    /**
     * Получение списка товара для категории
     *
     * @param categoryId
     * @return List<Item>
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/items")
    public List<Item> getCategoryItems(@PathVariable("id") String categoryId) {
        return categoryService.getCategoryItems(categoryId);
    }

    /**
     * Получение дерева категорий в виде бокового меню
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/side/menu")
    public List<Node> getSideMenuContent() {
        List<Node> sideMenu = Lists.newArrayList();

        //create root category node
        Node categoryRootNode = new Node();
        categoryRootNode.setTitle("Категории");
        categoryRootNode.setIsCompany(false);

        //fill with categories
        categoryRootNode.setNodes(getCategoryTree());

        //create root company node
        Node companyRootNode = new Node();
        companyRootNode.setTitle("Производители");
        companyRootNode.setIsCompany(true);

        //add child companies
        companyRootNode.getNodes().addAll(categoryService.getCompanyNodes());
        sideMenu.add(categoryRootNode);
        sideMenu.add(companyRootNode);
        return sideMenu;
    }

}
