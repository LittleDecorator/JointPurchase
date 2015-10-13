package com.acme.controller;

import com.acme.gen.domain.*;
import com.acme.gen.mapper.CategoryItemMapper;
import com.acme.gen.mapper.CategoryMapper;
import com.acme.gen.mapper.CompanyMapper;
import com.acme.gen.mapper.ItemMapper;
import com.acme.model.domain.CategoryItemLink;
import com.acme.model.domain.Node;
//import com.acme.model.mapper.CustomMapper;
import com.acme.model.mapper.CustomMapper;
import com.acme.service.TreeService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(value = "/category")
public class CategoryController {

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    TreeService treeService;

    @Autowired
    CategoryItemMapper categoryItemMapper;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    CompanyMapper companyMapper;

    @Autowired
    CustomMapper customMapper;

    /**
     * Get all categories as map
     **/
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

    /**
     * Get all categories as tree
     **/
    @RequestMapping(method = RequestMethod.GET,value = "/tree")
        public List<Node> getCategoryTree(){

        //old version
        /*List<CategoryItemLink> links = new ArrayList<>();
        CategoryItemLink link;
        List<String> list = Lists.newArrayList();
        List<Category> categories = categoryMapper.selectByExample(new CategoryExample());
        List<Item> types = itemMapper.selectByExample(new ItemExample());
        //get linked types
        List<CategoryItem> categoryTypes = categoryItemMapper.selectByExample(new CategoryItemExample());

        //merge lists into fake model
        for(Category category : categories){
            List<Item> typeList = Lists.newArrayList();
            link = new CategoryItemLink();
            link.setId(category.getId());
            link.setName(category.getName());
            link.setParentId(category.getParentId());

            Iterator<CategoryItem> categoryTypeIterator = categoryTypes.iterator();
            while(categoryTypeIterator.hasNext()){
                CategoryItem categoryType = categoryTypeIterator.next();
                if(categoryType.getCategoryId().contentEquals(category.getId())){
                    list.add(categoryType.getItemId());
                }
            }
            if(list.size()>0){
                Iterator<Item> typeIterator = types.iterator();
                while(typeIterator.hasNext()){
                    Item type = typeIterator.next();
                    if(list.contains(type.getId())){
                        typeList.add(type);
                    }
                }
            }

            link.setItems(typeList);
            links.add(link);
        }*/

        List<CategoryItemLink> links = customMapper.getCategoryTreeItems();

        return treeService.generateCategoryTree(links);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/tree")
    public void saveCategoryTree(@RequestBody String input) throws ParseException, IOException {
        JSONParser parser=new JSONParser();
        JSONObject main = (JSONObject) parser.parse(input);

        ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        JSONArray newArray = (JSONArray)main.get("new");
        if(newArray.size()>0){
            /* add new nodes */
            List<Node> newNodes = mapper.readValue(newArray.toJSONString(), new TypeReference<List<Node>>(){});

            Category category;
            CategoryItem categoryItem;

            for(Node node: newNodes){
                //create category
                category = new Category();
                category.setName(node.getTitle());
                category.setId(node.getId());
                category.setParentId(node.getParentId());
                categoryMapper.insertSelective(category);
                for(Item item : node.getItems()){
                    //link with type
                    categoryItem = new CategoryItem();
                    categoryItem.setCategoryId(category.getId());
                    categoryItem.setItemId(item.getId());
                    categoryItemMapper.insertSelective(categoryItem);
                }
            }
        }

        JSONArray updateArray = (JSONArray)main.get("update");
        if(updateArray.size()>0){
            List<Node> updateNodes = mapper.readValue(updateArray.toJSONString(), new TypeReference<List<Node>>(){});

            Category category;
            CategoryItem categoryItem;

            for(Node node: updateNodes){
                //create category
                category = new Category();
                category.setName(node.getTitle());
                category.setId(node.getId());
                category.setParentId(node.getParentId());
                categoryMapper.updateByPrimaryKeySelective(category);
                //get linked types
                CategoryItemExample example = new CategoryItemExample();
                example.createCriteria().andCategoryIdEqualTo(category.getId());

                List<String> oldLinks = Lists.transform(categoryItemMapper.selectByExample(example), categoryItemLink -> categoryItemLink.getItemId());

                List<String> newLinks = Lists.transform(node.getItems(), item -> item.getId());

                System.out.println("old type contains -> "+oldLinks);
                System.out.println("new type contains -> "+newLinks);

                Iterator<String> oldIt = oldLinks.iterator();
                while (oldIt.hasNext()){
                    String old = oldIt.next();
                    System.out.println("old type id -> "+old);
                    if(newLinks.contains(old)){
                        System.out.println("find in new");
                        oldIt.remove();
                        newLinks.remove(old);
                    }
                }
                System.out.println("Will be deleted -> "+ oldLinks);
                //remove old
                CategoryItemExample oldExample = new CategoryItemExample();
                oldExample.createCriteria().andItemIdIn(oldLinks);
                categoryItemMapper.deleteByExample(oldExample);

                //add new link with type
                for(String itemId : newLinks){
                    categoryItem = new CategoryItem();
                    categoryItem.setCategoryId(category.getId());
                    categoryItem.setItemId(itemId);
                    categoryItemMapper.insertSelective(categoryItem);
                }
            }
        }

        JSONArray deleteArray = (JSONArray)main.get("delete");
        if(deleteArray.size()>0){
            List<Node> deleteNodes = mapper.readValue(deleteArray.toJSONString(), new TypeReference<List<Node>>(){});

            for(Node node: deleteNodes){
                CategoryItemExample delExample = new CategoryItemExample();
                delExample.createCriteria().andCategoryIdEqualTo(node.getId());
                categoryItemMapper.deleteByExample(delExample);
                categoryMapper.deleteByPrimaryKey(node.getId());
            }

        }
    }

    /* get all types */
    @Deprecated
    @RequestMapping(method = RequestMethod.GET,value = "/types")
    public List getAllTypes(){
//        return typeMapper.selectByExample(new TypeExample());
        return null;
    }

    /* update or insert type */
    @Deprecated
    @RequestMapping(method = RequestMethod.POST,value = "/types")
    public Item addType(@RequestBody Item item){
//        if(!Strings.isNullOrEmpty(type.getId())){
//            typeMapper.updateByPrimaryKey(type);
//        } else {
//            typeMapper.insertSelective(type);
//        }
//        return type;
        return null;
    }

    /* delete type */
    @Deprecated
    @RequestMapping(method = RequestMethod.DELETE,value = "/types/{id}")
    public void addType(@PathVariable("id") String id){
//        typeMapper.deleteByPrimaryKey(id);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/side/menu")
    public List<Node> getSideMenuContent(){
        //fill with categories
        List<Node> nodes = getCategoryTree();
        //add companies

        //create root company node
        Node rootCompany = new Node();
        rootCompany.setTitle("Производители");
        rootCompany.setIsCompany(true);
        //add child companies
        Node node;
        for(Company company : companyMapper.selectByExample(new CompanyExample())){
            node = new Node();
            node.setTitle(company.getName());
            node.setId(company.getId());
            node.setIsCompany(true);
            rootCompany.getNodes().add(node);
        }
        nodes.add(rootCompany);
        return nodes;
    }

}
