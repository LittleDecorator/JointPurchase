//package com.acme.repository;
//
//import com.acme.constant.Queue;
//import com.acme.model.CategoryItem;
//import com.acme.repository.mapper.Mappers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public class ItemCategoryLinkRepository {
//
//    @Autowired
//    JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    NamedParameterJdbcTemplate parameterJdbcTemplate;
//
//    public List<CategorizeItem> getGetAll(){
//        return jdbcTemplate.query(Queue.ITEM_CATEGORY_LINK_FIND_ITEM_CATEGORIES, Mappers.itemCategoryLinkMapper);
//    }
//
//    public List<CategoryItem> getByItemId (String itemId){
//        return jdbcTemplate.query(Queue.ITEM_CATEGORY_FIND_BY_ITEM_ID, Mappers.categoryItemMapper, itemId);
//    }
//
//    public List<CategoryItem> getByItemIdList (List<String> itemIds){
//        System.out.println(itemIds);
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("ids", itemIds);
//        try{
//            return parameterJdbcTemplate.query(Queue.ITEM_CATEGORY_FIND_BY_ITEM_ID_LIST, parameters, Mappers.categoryItemMapper);
//        } catch (EmptyResultDataAccessException ex){
//            return null;
//        }
//
//    }
//
//    public List<CategoryItem> getByCategoryId (String categoryId){
//        return jdbcTemplate.query(Queue.ITEM_CATEGORY_FIND_BY_CATEGORY_ID, Mappers.categoryItemMapper, categoryId);
//    }
//
//    public List<CategoryItem> getByCategoryIdList (List<String> categoryIds){
//        System.out.println(categoryIds);
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("ids", categoryIds);
//        return parameterJdbcTemplate.query(Queue.ITEM_CATEGORY_FIND_BY_CATEGORY_ID_LIST, parameters, Mappers.categoryItemMapper);
//    }
//
//
//
//}