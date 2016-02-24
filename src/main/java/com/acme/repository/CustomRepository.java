package com.acme.repository;

import com.acme.config.Queue;
import com.acme.filter.ProductFilter;
import com.acme.model.Item;
import com.acme.model.Product;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
        StringBuilder sb = new StringBuilder();
        if(productFilter.getLimit()!=null){
            sb.append("limit " + productFilter.getLimit());
        }

        if(productFilter.getOffset()!=null){
            sb.append("offset " +productFilter.getOffset());
        }

        String queue = sb.toString();
        if(!Strings.isNullOrEmpty(queue)){
            queue = Queue.CUSTOM_FIND_ITEMS_BY_FILTER + queue;
        } else {
            queue = Queue.CUSTOM_FIND_ITEMS_BY_FILTER;
        }
        return jdbcTemplate.query(queue,productMapper);
    }

    private RowMapper<Product> productMapper = (rs,num) -> {
        Product product = new Product();
        product.setContentId(rs.getString("content_id"));

        Item item = new Item();
        item.setId(rs.getString("id"));
        item.setName(rs.getString("name"));
        item.setCompanyId(rs.getString("company_id"));
        item.setArticle(rs.getString("article"));
        item.setDescription(rs.getString("description"));
        item.setPrice(rs.getBigDecimal("price"));
        item.setNotForSale(rs.getBoolean("not_for_sale"));
        item.setInStock(rs.getInt("in_stock"));
        item.setDateAdd(rs.getDate("date_add"));

        product.setItem(item);

        return product;
    };





}
