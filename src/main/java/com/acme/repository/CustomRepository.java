package com.acme.repository;

import com.acme.constant.Queue;
import com.acme.model.dto.Product;
import com.acme.model.filter.CatalogFilter;
import com.acme.repository.mapper.Mappers;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class CustomRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate parameterJdbcTemplate;

//    public Integer getOrderedItemCou(String itemId){
//        try {
//            return jdbcTemplate.queryForObject(Queue.ITEM_GET_ORDERED_COU,Integer.class,itemId);
//        } catch (EmptyResultDataAccessException e) {
//            return null;
//        }
//    }

//    public List<String> getCategoriesMapByParentId(String parentId){
//        System.out.println(parentId);
//        return jdbcTemplate.queryForList(Queue.ITEM_BY_CATEGORY_AND_CHILDREN_NOT_FOR_SALE, String.class, parentId);
//    }

    public List<Product> getCatalog(CatalogFilter catalogFilter){
        String queue = Queue.GET_CATALOG;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("limit", catalogFilter.getLimit());
        parameters.addValue("offset", catalogFilter.getOffset());

        if(!Strings.isNullOrEmpty(catalogFilter.getCategory())){
            queue = Queue.ITEM_BY_CATEGORY_FOR_SALE;
            parameters.addValue("categoryId", catalogFilter.getCategory());
        }

        if(!Strings.isNullOrEmpty(catalogFilter.getCompany())){
            queue = Queue.ITEM_BY_COMPANY_FOR_SALE;
            parameters.addValue("companyId", catalogFilter.getCompany());
        }

        queue += " limit :limit offset :offset";
        return parameterJdbcTemplate.query(queue, parameters, Mappers.productMapper);
    }

    public List<Product> getBySearch(String criteria){
        Map<String,Object> namedParameters = Maps.newHashMap();
        namedParameters.put("criteria", "%"+criteria+"%");
        return parameterJdbcTemplate.query(Queue.FIND_BY_SEARCH,namedParameters, Mappers.productMapper);
    }



}
