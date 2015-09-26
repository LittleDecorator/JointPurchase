package com.acme.controller;

import com.acme.gen.domain.*;
import com.acme.gen.mapper.CategoryMapper;
import com.acme.gen.mapper.CategoryTypeMapper;
import com.acme.gen.mapper.CompanyMapper;
import com.acme.gen.mapper.TypeMapper;
import com.acme.helper.CategoryTypeLink;
import com.acme.model.domain.Node;
import com.acme.service.TreeService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
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
    CategoryTypeMapper categoryTypeMapper;

    @Autowired
    TypeMapper typeMapper;

    @Autowired
    CompanyMapper companyMapper;

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

    @RequestMapping(method = RequestMethod.GET,value = "/type/map")
    public List<Map<String, String>> getTypeMap() {
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map;

        for(Type type : typeMapper.selectByExample(new TypeExample())){
            map = new HashMap<>();
            map.put("id",type.getId());
            map.put("name",type.getName());
            list.add(map);
        }
        return list;
    }

    @RequestMapping(method = RequestMethod.GET,value = "/tree")
        public List<Node> getCategoryTree(){

        List<CategoryTypeLink> links = new ArrayList<>();
        CategoryTypeLink link;
        List<String> list = Lists.newArrayList();
        //TODO: move code below to custom mapper
        //get categories
        List<Category> categories = categoryMapper.selectByExample(new CategoryExample());
        //get all types
        List<Type> types = typeMapper.selectByExample(new TypeExample());
        //get linked types
        List<CategoryType> categoryTypes = categoryTypeMapper.selectByExample(new CategoryTypeExample());

        //merge lists into fake model
        for(Category category : categories){
            List<Type> typeList = Lists.newArrayList();
            link = new CategoryTypeLink();
            link.setId(category.getId());
            link.setName(category.getName());
            link.setParentId(category.getParentId());

            Iterator<CategoryType> categoryTypeIterator = categoryTypes.iterator();
            while(categoryTypeIterator.hasNext()){
                CategoryType categoryType = categoryTypeIterator.next();
                if(categoryType.getCategoryId().contentEquals(category.getId())){
                    list.add(categoryType.getTypeId());
                }
            }
            if(list.size()>0){
                Iterator<Type> typeIterator = types.iterator();
                while(typeIterator.hasNext()){
                    Type type = typeIterator.next();
                    if(list.contains(type.getId())){
                        typeList.add(type);
                    }
                }
            }

            link.setTypes(typeList);
            links.add(link);
        }
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
            CategoryType categoryType;

            for(Node node: newNodes){
                //create category
                category = new Category();
                category.setName(node.getTitle());
                category.setId(node.getId());
                category.setParentId(node.getParentId());
                categoryMapper.insertSelective(category);
                for(Type type : node.getTypes()){
                    //link with type
                    categoryType = new CategoryType();
                    categoryType.setCategoryId(category.getId());
                    categoryType.setTypeId(type.getId());
                    categoryTypeMapper.insertSelective(categoryType);
                }
            }
        }

        JSONArray updateArray = (JSONArray)main.get("update");
        if(updateArray.size()>0){
            List<Node> updateNodes = mapper.readValue(updateArray.toJSONString(), new TypeReference<List<Node>>(){});

            Category category;
            CategoryType categoryType;

            for(Node node: updateNodes){
                //create category
                category = new Category();
                category.setName(node.getTitle());
                category.setId(node.getId());
                category.setParentId(node.getParentId());
                categoryMapper.updateByPrimaryKeySelective(category);
                //get linked types
                CategoryTypeExample example = new CategoryTypeExample();
                example.createCriteria().andCategoryIdEqualTo(category.getId());

                List<String> oldTypes = Lists.transform(categoryTypeMapper.selectByExample(example), new Function<CategoryType, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable CategoryType categoryType) {
                        return categoryType.getTypeId();
                    }
                });

                List<String> newTypes = Lists.transform(node.getTypes(), new Function<Type, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable Type categoryType) {
                        return categoryType.getId();
                    }
                });

                System.out.println("old type contains -> "+oldTypes);
                System.out.println("new type contains -> "+newTypes);

                Iterator<String> oldIt = oldTypes.iterator();
                while (oldIt.hasNext()){
                    String old = oldIt.next();
                    System.out.println("old type id -> "+old);
                    if(newTypes.contains(old)){
                        System.out.println("find in new");
                        oldIt.remove();
                        newTypes.remove(old);
                    }
                }
                System.out.println("Will be deleted -> "+ oldTypes);
                //remove old
                CategoryTypeExample oldExample = new CategoryTypeExample();
                oldExample.createCriteria().andTypeIdIn(oldTypes);
                categoryTypeMapper.deleteByExample(oldExample);

                //add new link with type
                for(String typeId : newTypes){
                    categoryType = new CategoryType();
                    categoryType.setCategoryId(category.getId());
                    categoryType.setTypeId(typeId);
                    categoryTypeMapper.insertSelective(categoryType);
                }
            }
        }

        JSONArray deleteArray = (JSONArray)main.get("delete");
        if(deleteArray.size()>0){
            List<Node> deleteNodes = mapper.readValue(deleteArray.toJSONString(), new TypeReference<List<Node>>(){});

            for(Node node: deleteNodes){
                CategoryTypeExample delExample = new CategoryTypeExample();
                delExample.createCriteria().andCategoryIdEqualTo(node.getId());
                categoryTypeMapper.deleteByExample(delExample);
                categoryMapper.deleteByPrimaryKey(node.getId());
            }

        }
    }

    /* get all types */
    @RequestMapping(method = RequestMethod.GET,value = "/types")
    public List getAllTypes(){
        return typeMapper.selectByExample(new TypeExample());
    }

    /* update or insert type */
    @RequestMapping(method = RequestMethod.POST,value = "/types")
    public Type addType(@RequestBody Type type){
        if(!Strings.isNullOrEmpty(type.getId())){
            typeMapper.updateByPrimaryKey(type);
        } else {
            typeMapper.insertSelective(type);
        }
        return type;
    }

    /* delete type */
    @RequestMapping(method = RequestMethod.DELETE,value = "/types/{id}")
    public void addType(@PathVariable("id") String id){
        typeMapper.deleteByPrimaryKey(id);
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
