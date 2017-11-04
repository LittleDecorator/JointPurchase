package com.acme.controller;

import com.acme.exception.PersistException;
import com.acme.model.*;
import com.acme.model.dto.CategoryTransfer;
import com.acme.model.dto.MapDto;
import com.acme.service.CategoryService;
import com.acme.service.ItemService;
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
@RequestMapping(value = "/api/category")
public class CategoryController {

    @Autowired
    TreeService treeService;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ItemService itemService;

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
     * @param name
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{name}")
    public Category getCategory(@PathVariable(value = "name") String name) {
        return categoryService.getCategoryByName(name);
    }

    /**
     * Получение полного списка зависимых категорий
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/children")
    public List<MapDto> getChildren(@PathVariable(value = "id") String id) {
        List<MapDto> result = Lists.newArrayList(new MapDto(id, "Все"));
        result.addAll(categoryService.getChildren(id));
        return result;
    }

    /**
     * Получение поставщиков для категорий
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/children/company")
    public List<MapDto> getChildrenCompanies(@PathVariable(value = "id") String id) {
        List<MapDto> result = Lists.newArrayList(new MapDto(null, "Все"));
        List<String> categoryIds = categoryService.getChildren(id).stream().map(MapDto::getId).collect(Collectors.toList());
        List<Item> items = categoryService.getCategoryItems(categoryIds);
        //List<Item> items = itemService.getAllByCategoryIdList(categoryIds);

        result.addAll(items.stream().map(item -> {
            Company company = item.getCompany();
                return new MapDto(company.getId(), company.getName());
        }).distinct().collect(Collectors.toList()));
        return result;
    }

    /**
     * Получение мапы категории
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/map")
    public List<MapDto> getCategoryMap() {
        List<MapDto> result = Lists.newArrayList();
        result.addAll(categoryService.getAll().stream().map(category -> new MapDto(category.getId(), category.getName())).collect(Collectors.toList()));
        return result;
    }

    /**
     * Получение корневых узлов дерева категорий в виде мапы
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/map/roots")
    public List<MapDto> getCategoryRootMap() {
        List<MapDto> result = Lists.newArrayList();
        result.addAll(categoryService.getAll().stream().filter(category -> Strings.isNullOrEmpty(category.getParentId())).map(category -> new MapDto(category.getId(), category.getName())).collect(Collectors.toList()));
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
     * @param name
     * @return List<Item>
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{name}/items")
    public List<Item> getCategoryItems(@PathVariable("name") String name) {
        return categoryService.getCategoryItemsByName(name);
    }

    @Transactional
    @RequestMapping(method = RequestMethod.PATCH, value = "translite")
    public void transliteItems(@RequestParam(name = "all", required = false, defaultValue = "true") Boolean all){
        categoryService.transliteCategories(all);
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
