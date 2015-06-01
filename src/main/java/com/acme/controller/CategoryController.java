package com.acme.controller;

import com.acme.gen.domain.Category;
import com.acme.gen.domain.CategoryExample;
import com.acme.gen.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/category")
public class CategoryController {

    @Autowired
    CategoryMapper categoryMapper;

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
}
