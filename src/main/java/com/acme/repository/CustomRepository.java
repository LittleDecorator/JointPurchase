package com.acme.repository;

import com.acme.constant.Queue;
import com.acme.model.Product;
import com.acme.model.filter.ProductFilter;
import com.acme.repository.mapper.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate parameterJdbcTemplate;

    public Integer getOrderedItemCou(String itemId){
        try {
            return jdbcTemplate.queryForObject(Queue.ITEM_GET_ORDERED_COU,Integer.class,itemId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<String> getCategoriesMapByParentId(String parentId){
        System.out.println(parentId);
        return jdbcTemplate.queryForList(Queue.ITEM_BY_CATEGORY_AND_CHILDREN_NOT_FOR_SALE, String.class, parentId);
    }

    public List<Product> getItemsByFilter(ProductFilter productFilter, List<String> categorties){
        System.out.println(productFilter.toString());

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("limit", productFilter.getLimit());
        parameters.addValue("offset", productFilter.getOffset());
        parameters.addValue("categoryIds", categorties);

        String queue = Queue.CUSTOM_FIND_ITEMS;

        if(categorties != null && !categorties.isEmpty()){
            queue += " AND ct.id IN ( :categoryIds )";
        }

        queue += " limit :limit offset :offset";
        return parameterJdbcTemplate.query(queue, parameters, Mappers.productMapper);
    }

}
