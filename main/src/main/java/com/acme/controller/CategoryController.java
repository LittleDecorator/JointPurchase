package com.acme.controller;

import com.acme.gen.domain.*;
import com.acme.gen.mapper.CategoryItemMapper;
import com.acme.gen.mapper.CategoryMapper;
import com.acme.gen.mapper.CompanyMapper;
import com.acme.gen.mapper.ItemMapper;
import com.acme.model.domain.Node;
//import com.acme.model.mapper.CustomMapper;
import com.acme.model.mapper.CustomMapper;
import com.acme.service.TreeService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
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
    CategoryItemMapper categoryItemMapper;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    CompanyMapper companyMapper;

    @Autowired
    CustomMapper customMapper;

    /**
     *
     * @return all categories as map
     */
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
     * @return list of nodes, which just category representation
     */
    @RequestMapping(method = RequestMethod.GET,value = "/tree")
        public List<Node> getCategoryTree(){
        List<Category> list = categoryMapper.selectByExample(new CategoryExample());
        return treeService.generateCategoryTree(list);
    }

    /**
     * Save given category tree
     *
     * @param input
     * @throws ParseException
     * @throws IOException
     */
    //TODO: ADD Items processing in tree
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
                if(oldLinks.size()>0){
                    CategoryItemExample oldExample = new CategoryItemExample();
                    oldExample.createCriteria().andItemIdIn(oldLinks);
                    categoryItemMapper.deleteByExample(oldExample);
                }

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

    /**
     * Return all items as custom array for given category
     * @param categoryId
     * @return JSONArray
     */
    @RequestMapping(method = RequestMethod.GET,value = "/items/{id}")
    public JSONArray getCategoryItems(@PathVariable("id") String categoryId){
        CategoryItemExample example = new CategoryItemExample();
        example.createCriteria().andCategoryIdEqualTo(categoryId);
        List<String> list = Lists.transform(categoryItemMapper.selectByExample(example), new Function<CategoryItem, String>() {
            @Nullable
            @Override
            public String apply(CategoryItem categoryItem) {
                return categoryItem.getItemId();
            }
        });

        JSONArray array = new JSONArray();
        if(list.size()>0){
            ItemExample itemExample = new ItemExample();
            itemExample.createCriteria().andIdIn(list);


            JSONObject object;
            for(Item item : itemMapper.selectByExample(itemExample)){
                object = new JSONObject(new HashMap(3));
                object.put("id",item.getId());
                object.put("name",item.getName());
                object.put("article",item.getArticle());
                array.add(object);
            }
        }
        return array;
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
        System.out.println(nodes.size());
        nodes.add(rootCompany);
        System.out.println(nodes.size());
        return nodes;
    }

}
