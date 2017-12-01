package com.acme.service.impl;

import com.acme.exception.PersistException;
import com.acme.model.*;
import com.acme.model.dto.CategoryTransfer;
import com.acme.model.dto.MapDto;
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
import com.google.common.collect.Lists;
import com.ibm.icu.text.Transliterator;
import java.util.stream.Stream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static String RUSSIAN_TO_LATIN_BGN = "Russian-Latin/BGN";

    @Autowired
    TreeService treeService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    CategoryItemRepository categoryItemRepository;

    @Override
    public List<Category> getAll() {
        return Lists.newArrayList(categoryRepository.findAll());
    }

    @Override
    public List<Category> getAllByParentId(String parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    @Override
    public List<Category> getAllByIdIn(List<String> ids) {
        return categoryRepository.findByIdIn(ids);
    }

    @Override
    public Category getCategory(String categoryId) {
        return categoryRepository.findOne(categoryId);
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findOneByTransliteName(name);
    }

    @Override
    public List<Item> getCategoryItems(String categoryId) {
        List<CategoryItem> categoryItems = categoryItemRepository.findAllByCategoryId(categoryId);
        List<String> itemIds = categoryItems.stream().map(CategoryItem::getItemId).collect(Collectors.toList());
        return itemRepository.findByIdIn(itemIds);
    }

    @Override
    public List<Item> getCategoryItems(List<String> categoryIds) {
        List<CategoryItem> categoryItems = categoryItemRepository.findAllByCategoryIdIn(categoryIds);
        List<String> itemIds = categoryItems.stream().map(CategoryItem::getItemId).collect(Collectors.toList());
        return itemRepository.findByIdIn(itemIds);
    }

    @Override
    public List<Item> getCategoryItemsByName(String name) {
        Category category = categoryRepository.findOneByTransliteName(name);
        List<CategoryItem> categoryItems = categoryItemRepository.findAllByCategoryId(category.getId());
        List<String> itemIds = categoryItems.stream().map(CategoryItem::getItemId).collect(Collectors.toList());
        return itemRepository.findByIdIn(itemIds);
    }

    @Override
    public List<Category> getRootCategories(String companyId) {
        return categoryRepository.getRootCategories(companyId);
    }

    @Override
    public void createCategory(CategoryTransfer transfer) {
        Category category = new Category();
        category.setName(transfer.getName());
        category.setParentId(transfer.getParentId());
        category.setItems(itemRepository.findByIdIn(transfer.getItems()));
        categoryRepository.save(category);
    }

    @Override
    public Category createCategoryFromNode(Node node) {
        Category category = new Category();
        category.setName(node.getTitle());
        category.setId(node.getId());
        category.setParentId(node.getParentId());
        category.setDescription(node.getDescription());
        category.setTransliteName(node.getName());
        category.setDateAdd(new Date());
        categoryRepository.save(category);
        return category;
    }

    @Override
    public void deleteCategory(String categoryId) {
        categoryItemRepository.deleteByCategoryId(categoryId);
        categoryRepository.delete(categoryId);
    }

    @Override
    public List<CategoryItem> createCategoryItemList4Category(String categoryId, List<String> itemIdList) {
        List<CategoryItem> categoryItems = Lists.newArrayList();
        categoryItems.addAll(itemIdList.stream().map(itemId -> createCategoryItem(itemId, categoryId)).collect(Collectors.toList()));
        return categoryItems;
    }

    @Override
    public List<CategoryItem> createCategoryItemList4Item(String itemId, List<String> categoryIdList) {
        List<CategoryItem> categoryItems = Lists.newArrayList();
        categoryItems.addAll(categoryIdList.stream().map(categoryId -> createCategoryItem(itemId, categoryId)).collect(Collectors.toList()));
        return categoryItems;
    }

    @Override
    public void persistCategoryTree(JSONObject main) throws IOException, ParseException, PersistException {
        ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        /* добавление новых узлов в дерево */
        JSONArray newArray = (JSONArray)main.get("new");
        if(newArray.size()>0){
            List<Node> newNodes = mapper.readValue(newArray.toJSONString(), new TypeReference<List<Node>>(){});

            for(Node node: newNodes){
                Category category = createCategoryFromNode(node);

                /* Добавим объекты связи с товаром*/
                if(node.getItems() !=null ){
                    List<CategoryItem> categoryItemList = Lists.newArrayList();
                    categoryItemList.addAll(node.getItems().stream().map(item -> createCategoryItem(item.getId(), category.getId())).collect(Collectors.toList()));
                    categoryItemRepository.save(categoryItemList);
                }
            }
        }

        /* Обновление узлов дерева */
        JSONArray updateArray = (JSONArray)main.get("update");
        if(updateArray.size()>0){
            List<Node> updateNodes = mapper.readValue(updateArray.toJSONString(), new TypeReference<List<Node>>(){});

            for(Node node: updateNodes){
                Category category = createCategoryFromNode(node);
                /* удаление старых связей и создание новых */
                if(node.getItems() == null){
                    categoryItemRepository.deleteByCategoryId(category.getId());
                } else {
                    List<String> itemIds = node.getItems().stream().map(Item::getId).collect(Collectors.toList());
                    categoryItemRepository.deleteByCategoryIdAndItemIdNotIn(category.getId(), itemIds);
                    List<CategoryItem> categoryItemList = Lists.newArrayList();
                    categoryItemList.addAll(node.getItems().stream().map(item -> createCategoryItem(item.getId(), category.getId())).collect(Collectors.toList()));
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
                deleteCategory(node.getId());
            }
        }
    }

    @Override
    public List<Node> getRootNodes() {
        List<Node> roots = Lists.newArrayList();
        roots.addAll(Lists.newArrayList(categoryRepository.findAll()).stream().filter(category -> Strings.isNullOrEmpty(category.getParentId())).map(category -> treeService.category2Node(category)).collect(Collectors.toList()));
        return roots;
    }

    @Override
    public List<Node> getCompanyNodes() {
        List<Node> nodes = Lists.newArrayList();
        for(Company company : companyRepository.findAll()){
            Node node = new Node(company.getId(), company.getTransliteName(),  company.getName());
            node.setIsCompany(true);
            nodes.add(node);
        }
        return nodes;
    }

    @Override
    public List<MapDto> getChildren(String categoryId) {
        return categoryRepository.getChildCategories(categoryId).stream()
                .map(category -> new MapDto(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public void transliteCategories(boolean all) {
        Stream<Category> categories = getAll().stream();
        if(!all){
            // фильтруем если нужно
            categories = categories.filter(category -> Strings.isNullOrEmpty(category.getTransliteName()));

        }
        // обновляем
        categories.forEach(category -> {
            category.setTransliteName(translite(category.getName()));
            categoryRepository.save(category);
        });
    }

    /**
     *
     * @param input
     * @return
     */
    private String translite(String input){
        Transliterator russianToLatinNoAccentsTrans = Transliterator.getInstance(RUSSIAN_TO_LATIN_BGN);
        return russianToLatinNoAccentsTrans.transliterate(input).replaceAll("·|ʹ|\\.|\"|,|\\(|\\)", "").replaceAll("\\*|\\s+","-").toLowerCase();
    }

    private CategoryItem createCategoryItem(String itemId, String categoryId){
        CategoryItem categoryItem = new CategoryItem();
        categoryItem.setCategoryId(categoryId);
        categoryItem.setItemId(itemId);
        categoryItem.setDateAdd(new Date());
        return categoryItem;
    }
}
