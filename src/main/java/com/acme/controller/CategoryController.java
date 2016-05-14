package com.acme.controller;

import com.acme.exception.PersistException;
import com.acme.model.*;
import com.acme.repository.CategoryItemRepository;
import com.acme.repository.CategoryRepository;
import com.acme.repository.CompanyRepository;
import com.acme.repository.ItemRepository;
import com.acme.service.TreeService;
import com.acme.service.impl.TreeServiceImpl;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
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
    CategoryRepository categoryRepository;

    @Autowired
    TreeService treeService;

    @Autowired
    CategoryItemRepository categoryItemRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    CompanyRepository companyRepository;

    /**
     *
     * @return all categories as map
     */
    @RequestMapping(method = RequestMethod.GET,value = "/map")
    public List<Map<String, Object>> getCategoryMap() {
        return categoryRepository.getNameMap();
    }

    @RequestMapping(method = RequestMethod.GET,value = "/map/roots")
    public List<Map<String, Object>> getCategoryRootMap() {
        return categoryRepository.getRootsAsMap();
    }

    /**
     * Get all categories as tree
     * @return list of nodes, which just category representation
     */
    @RequestMapping(method = RequestMethod.GET,value = "/tree")
        public List<Node> getCategoryTree(){
        List<Category> list = categoryRepository.getAll();
        /* old version with full tree */
//        return treeService.generateCategoryTree(list);

        /* for now try to use only roots */
        List<Node> roots = Lists.newArrayList();
        for(Category category : list){
            if(Strings.isNullOrEmpty(category.getParentId())){
                roots.add(treeService.category2Node(category));
            }
        }
        return roots;
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
    public void saveCategoryTree(@RequestBody String input) throws IOException, ParseException, PersistException {
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
                if(!categoryRepository.insert(category)){
                    throw new PersistException(String.format(" Can't insert new Category -> %s", category.toString()));
                }
                for(Item item : node.getItems()){
                    //link with type
                    categoryItem = new CategoryItem();
                    categoryItem.setCategoryId(category.getId());
                    categoryItem.setItemId(item.getId());
                    categoryItemRepository.insert(categoryItem);
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
                categoryRepository.update(category);

                //get linked types
                List<String> oldLinks = Lists.transform(categoryItemRepository.getByCategoryId(category.getId()), CategoryItem::getItemId);
                List<String> newLinks = Lists.transform(node.getItems(), Item::getId);

                Iterator<String> oldIt = oldLinks.iterator();
                while (oldIt.hasNext()){
                    String old = oldIt.next();
                    if(newLinks.contains(old)){
                        oldIt.remove();
                        newLinks.remove(old);
                    }
                }
                //remove old
                if(oldLinks.size()>0){
                    categoryItemRepository.deleteByItemIdList(oldLinks);
                }

                //add new link with type
                for(String itemId : newLinks){
                    categoryItem = new CategoryItem();
                    categoryItem.setCategoryId(category.getId());
                    categoryItem.setItemId(itemId);
                    categoryItemRepository.insert(categoryItem);
                }
            }
        }

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
     * Return all items as custom array for given category
     *
     * @param categoryId
     * @return JSONArray
     */
    @RequestMapping(method = RequestMethod.GET,value = "/items/{id}")
    public JSONArray getCategoryItems(@PathVariable("id") String categoryId){
        List<String> list = Lists.transform(categoryItemRepository.getByCategoryId(categoryId), CategoryItem::getItemId);

        JSONArray array = new JSONArray();
        if(list.size()>0){
            JSONObject object;
            for(Item item : itemRepository.getByIdList(list)){
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
        for(Company company : companyRepository.getAll()){
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

}
