package com.acme.repository;

import com.acme.config.Queue;
import com.acme.model.Product;
import com.acme.model.filter.ProductFilter;
import com.acme.repository.mapper.Mappers;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Integer getOrderedItemCou(String itemId){
        try {
            return jdbcTemplate.queryForObject(Queue.ITEM_GET_ORDERED_COU,Integer.class,itemId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Product> getItemsByFilter(ProductFilter productFilter){
        System.out.println(productFilter.toString());
        StringBuilder sb = new StringBuilder();
        if(productFilter.getLimit()!=null){
            sb.append(" limit " + productFilter.getLimit());
        }

        if(productFilter.getOffset()!=null){
            sb.append(" offset " +productFilter.getOffset());
        }

        String queue = sb.toString();
        /*if(!Strings.isNullOrEmpty(queue)){
            queue = Queue.CUSTOM_FIND_ITEMS_BY_FILTER + queue;
        } else {
            queue = Queue.CUSTOM_FIND_ITEMS_BY_FILTER;
        }*/

        System.out.println(productFilter.getCategory());

        if(!Strings.isNullOrEmpty(productFilter.getCategory())){
            SqlParameterSource parameter = new MapSqlParameterSource("parentId",productFilter.getCategory());
            queue = Queue.ITEM_BY_CATEGORY_AND_CHILDREN_NOT_FOR_SALE + queue;
            return jdbcTemplate.query(queue, Mappers.productMapper, parameter);
        } else {
            queue = Queue.CUSTOM_FIND_ITEMS_BY_FILTER;
        }
        return jdbcTemplate.query(queue, Mappers.productMapper);
    }







}
