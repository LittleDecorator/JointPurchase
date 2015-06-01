package com.acme.controller;

import com.acme.gen.domain.*;
import com.acme.gen.mapper.CompanyMapper;
import com.acme.gen.mapper.ItemMapper;
import com.acme.gen.mapper.OrderItemsMapper;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/item")
public class ItemController{

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    OrderItemsMapper orderItemsMapper;

    @Autowired
    CompanyMapper companyMapper;

    @RequestMapping(method = RequestMethod.GET)
    public List<Item> getGoods() {
        return itemMapper.selectByExample(new ItemExample());
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public Item getGoodById(@PathVariable("id") String id) {
        return itemMapper.selectByPrimaryKey(id);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/order/{id}")
    public List<Item> getGoodsByOrderId(@PathVariable("id") String orderId) {
        ItemExample itemExample = new ItemExample();
        OrderItemsExample orderItemsExample = new OrderItemsExample();
        orderItemsExample.createCriteria().andOrderIdEqualTo(orderId);
        List<String> items = Lists.transform(orderItemsMapper.selectByExample(orderItemsExample), new Function<OrderItems, String>() {
            @Override
            public String apply(OrderItems orderItems) {
                return orderItems.getItemId();
            }
        });
        itemExample.createCriteria().andIdIn(items);
        return itemMapper.selectByExample(itemExample);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/company/{id}")
    public List<Item> getGoodsByCompanyId(@PathVariable("id")String companyId) {
        ItemExample itemExample = new ItemExample();
        itemExample.createCriteria().andCompanyIdEqualTo(companyId);
        return itemMapper.selectByExample(itemExample);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/map")
    public List<Map<String,String>> getItemMap() {
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map;
        for(Item i : itemMapper.selectByExample(new ItemExample())){
            map = new HashMap<>();
            map.put("id",i.getId());
            map.put("name",i.getName());
            list.add(map);
        }
        return list;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Item addGood(@RequestBody Item item) {
        if(item.getId()!=null){
            itemMapper.updateByPrimaryKeySelective(item);
        } else {
            itemMapper.insertSelective(item);
        }
        return item;
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public boolean deleteGood(@PathVariable("id") String id) {
        //delete item in orders
        OrderItemsExample orderItemsExample = new OrderItemsExample();
        orderItemsExample.createCriteria().andItemIdEqualTo(id);
        orderItemsMapper.deleteByExample(orderItemsExample);
        //delete item itself
        itemMapper.deleteByPrimaryKey(id);
        return true;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/filter")
    public List<Item> createOrder(@RequestBody String input) throws ParseException, IOException {
        System.out.println(input);
        JSONParser parser=new JSONParser();
        JSONObject main = (JSONObject) parser.parse(input);
        System.out.println(main);

        ItemExample itemExample = new ItemExample();
        ItemExample.Criteria criteria= itemExample.createCriteria();

        String name = main.get("name").toString();
        String company = main.get("company").toString();
        String category = main.get("category").toString();
        String article = main.get("article").toString();
//
        if(!Strings.isNullOrEmpty(name)){
            criteria.andNameLike("%"+name+"%");
        }
        if(!Strings.isNullOrEmpty(company)){
            criteria.andCompanyIdEqualTo(company);
        }
        if(!Strings.isNullOrEmpty(category)){
            criteria.andCategoryIdEqualTo(category);
        }
        if(!Strings.isNullOrEmpty(article)){
            criteria.andArticleLike(article);
        }

        return itemMapper.selectByExample(itemExample);
    }
}

