package com.acme.controller;

import com.acme.exception.PersistException;
import com.acme.model.*;
import com.acme.model.dto.CategoryTransfer;
import com.acme.repository.CategoryItemRepository;
import com.acme.repository.CategoryRepository;
import com.acme.repository.CompanyRepository;
import com.acme.repository.ItemRepository;
import com.acme.service.CategoryService;
import com.acme.service.TreeService;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.json.simple.JSONArray;
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
    CategoryRepository categoryRepository;

    @Autowired
    TreeService treeService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    CategoryItemRepository categoryItemRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    CategoryService categoryService;

    /**
     * Добавление/Обновление новой категории
     * @param transfer
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    public void createCategory(@RequestBody CategoryTransfer transfer){
        if(transfer!=null){
            TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
            try{
                Category category = new Category();
                category.setName(transfer.getName());
                category.setParentId(transfer.getParentId());
                category.setItems(itemRepository.findByIdIn(transfer.getItems()));
                categoryRepository.save(category);
                transactionManager.commit(status);
            } catch (Exception ex){
                transactionManager.rollback(status);
            }
        }
    }

    /**
     * Удаление категории по ID
     * @param id
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public void deleteCategory(@PathVariable("id") String id){
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            categoryRepository.delete(id);
            transactionManager.commit(status);
        } catch (Exception e){
            transactionManager.rollback(status);
        }
    }

    /**
     * Получение категории по ID
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public Category getCategory(@PathVariable(value = "id") String id){
        return categoryRepository.findOne(id);
    }

    /**
     * Получение мапы категории
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/map")
    public List<CategoryMap> getCategoryMap() {
        List<CategoryMap> result = Lists.newArrayList();
        for(Category category : categoryRepository.findAll()){
            result.add(new CategoryMap(category.getId(), category.getName()));
        }
        return result;
    }

    /**
     * Получение корневых узлов дерева категорий в виде мапы
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/map/roots")
    public List<CategoryMap> getCategoryRootMap() {
        List<CategoryMap> result = Lists.newArrayList();
        for(Category category : categoryRepository.findAll()){
            if(Strings.isNullOrEmpty(category.getParentId())){
                result.add(new CategoryMap(category.getId(), category.getName()));
            }
        }
        return result;
    }

    /**
     * Получение ветки дерева категорий глубиной в 1
     * @param parentId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/sub")
    public List<Category> getSubCategories(@PathVariable(value = "id") String parentId){
        return categoryRepository.findByParentId(parentId);
    }

    /**
     * Получение дерева категорий
     * @return list of nodes, which just category representation
     */
    @RequestMapping(method = RequestMethod.GET,value = "/tree")
        public List<Node> getCategoryTree(){
        List<Category> list = Lists.newArrayList(categoryRepository.findAll());
        return treeService.generateCategoryTree(list);
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
    @RequestMapping(method = RequestMethod.POST,value = "/tree")
    public void saveCategoryTree(@RequestBody String input) throws IOException, ParseException, PersistException {
        JSONParser parser=new JSONParser();
        JSONObject main = (JSONObject) parser.parse(input);

        ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        /* добавление новых узлов в дерево */
        JSONArray newArray = (JSONArray)main.get("new");
        if(newArray.size()>0){
            List<Node> newNodes = mapper.readValue(newArray.toJSONString(), new TypeReference<List<Node>>(){});

            for(Node node: newNodes){
                Category category = new Category();
                category.setName(node.getTitle());
                category.setId(node.getId());
                category.setParentId(node.getParentId());
                categoryRepository.save(category);

                /* Добавим объекты связи с товаром*/
                if(node.getItems() !=null ){
                    List<CategoryItem> categoryItemList = Lists.newArrayList();
                    for(Item item : node.getItems()){
                        CategoryItem categoryItem = new CategoryItem();
                        categoryItem.setCategoryId(category.getId());
                        categoryItem.setItemId(item.getId());
                        categoryItem.setDateAdd(new Date());
                        categoryItemList.add(categoryItem);
                    }
                    categoryItemRepository.save(categoryItemList);
                }
            }
        }

        /* Обновление узлов дерева */
        JSONArray updateArray = (JSONArray)main.get("update");
        if(updateArray.size()>0){
            List<Node> updateNodes = mapper.readValue(updateArray.toJSONString(), new TypeReference<List<Node>>(){});

            Category category;

            for(Node node: updateNodes){
                category = new Category();
                category.setName(node.getTitle());
                category.setId(node.getId());
                category.setParentId(node.getParentId());
                categoryRepository.save(category);
                /* удаление старых связей и создание новых */
                if(node.getItems() == null){
                    categoryItemRepository.deleteByCategoryId(category.getId());
                } else {
                    List<String> itemIds = node.getItems().stream().map(Item::getId).collect(Collectors.toList());
                    categoryItemRepository.deleteByCategoryIdAndItemIdNotIn(category.getId(), itemIds);
                    List<CategoryItem> categoryItemList = Lists.newArrayList();
                    for(Item item : node.getItems()){
                        CategoryItem categoryItem = new CategoryItem();
                        categoryItem.setCategoryId(category.getId());
                        categoryItem.setItemId(item.getId());
                        categoryItem.setDateAdd(new Date());
                        categoryItemList.add(categoryItem);
                    }
                    categoryItemRepository.save(categoryItemList);
                }
            }
        }

        /* удаление узлов */
        JSONArray deleteArray = (JSONArray)main.get("delete");
        if(deleteArray.size()>0){
            List<Node> deleteNodes = mapper.readValue(deleteArray.toJSONString(), new TypeReference<List<Node>>(){});

            for(Node node: deleteNodes){
                categoryItemRepository.deleteByCategoryId(node.getId());
                categoryRepository.delete(node.getId());
            }
        }
    }

    /**
     * Получение списка товара для категории
     *
     * @param categoryId
     * @return List<Item>
     */
    @RequestMapping(method = RequestMethod.GET,value = "/{id}/items")
    public List<Item> getCategoryItems(@PathVariable("id") String categoryId){
        List<CategoryItem> categoryItems = categoryItemRepository.findAllByCategoryId(categoryId);
        List<String> itemIds = categoryItems.stream().map(CategoryItem::getItemId).collect(Collectors.toList());
        return itemRepository.findByIdIn(itemIds);
    }

    /**
     * Получение дерева категорий в виде бокового меню
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/side/menu")
    public List<Node> getSideMenuContent(){
        Node node;
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
        for(Company company : companyRepository.findAll()){
            node = new Node();
            node.setTitle(company.getName());
            node.setId(company.getId());
            node.setIsCompany(true);
            companyRootNode.getNodes().add(node);
        }

        sideMenu.add(categoryRootNode);
        sideMenu.add(companyRootNode);
        return sideMenu;
    }

    /*---------- NESTED ----------*/

    /**
     * Класс предоставляющий данные для списка слиентов
     */
    private class CategoryMap {

        String id;
        String name;

        CategoryMap(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
