package com.acme.repository;

import com.acme.constant.Queue;
import com.acme.repository.mapper.Mappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class CategoryItemRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate parameterJdbcTemplate;

//    public boolean insert(CategoryItem categoryItem){
//        categoryItem.setId(UUID.randomUUID().toString());
//        int result = jdbcTemplate.update(Queue.CATEGORY_ITEM_INSERT,categoryItem.getId(),categoryItem.getCategoryId(),categoryItem.getItemId(),categoryItem.getDateAdd());
//        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
//    }
//
//    public void insertBulk(List<CategoryItem> categoryItems){
//        jdbcTemplate.batchUpdate(Queue.CATEGORY_ITEM_INSERT,
//                new BatchPreparedStatementSetter() {
//                    @Override
//                    public void setValues(PreparedStatement ps, int i) throws SQLException {
//                        CategoryItem categoryItem = categoryItems.get(i);
//                        ps.setString(1, UUID.randomUUID().toString());
//                        ps.setString(2, categoryItem.getCategoryId());
//                        ps.setString(3, categoryItem.getItemId());
//                        ps.setDate(4, null);
//                    }
//
//                    @Override
//                    public int getBatchSize() {
//                        return categoryItems.size();
//                    }
//                });
//    }
//
//    public List<CategoryItem> getByItemId(String itemId){
//        return jdbcTemplate.query(Queue.CATEGORY_ITEM_FIND_BY_ITEM_ID, Mappers.categoryItemMapper,itemId);
//    }
//
//    public List<CategoryItem> getByCategoryId(String categoryId){
//        return jdbcTemplate.query(Queue.CATEGORY_ITEM_FIND_BY_CATEGORY_ID, Mappers.categoryItemMapper,categoryId);
//    }

    public boolean deleteByCategoryId(String categoryID){
        int result = jdbcTemplate.update(Queue.CATEGORY_ITEM_DELETE_BY_CATEGORY_ID,categoryID);
        return result == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    public boolean deleteByItemId(String itemId){
        int result = jdbcTemplate.update(Queue.CATEGORY_ITEM_DELETE_BY_ITEM_ID,itemId);
        return result == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    public boolean deleteByItemIdList(List<String> itemIdList){
        SqlParameterSource namedParameters = new MapSqlParameterSource("itemIdList", itemIdList);
        int result = parameterJdbcTemplate.update(Queue.CATEGORY_ITEM_DELETE_BY_ITEM_LIST,namedParameters);
        return result == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    public boolean deleteByItemAndExcludedCategoryIdList(String itemId, List<String> categoryIdList){
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("categoryIdList", categoryIdList);
        parameters.addValue("itemId", itemId);
        int result = parameterJdbcTemplate.update(Queue.CATEGORY_ITEM_DELETE_BY_ITEM_AND_EXCLUDE_CATEGORY_LIST,parameters);
        return result == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    public boolean deleteByCategoryAndExcludedItemIdList(String categoryId, List<String> itemIdList){
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("itemIdList", itemIdList);
        parameters.addValue("categoryId", categoryId);
        int result = parameterJdbcTemplate.update(Queue.CATEGORY_ITEM_DELETE_BY_CATEGORY_AND_EXCLUDE_ITEM_LIST,parameters);
        return result == 0 ? Boolean.FALSE : Boolean.TRUE;
    }



//    public boolean insertSelective(CategoryItem categoryItem){
//        Boolean result = Boolean.FALSE;
//
//        Map<String,Object> namedParameters = Maps.newHashMap();
//        List<String> into = Lists.newArrayList();
//        List<String> values = Lists.newArrayList();
//
//        // for id property
//        into.add(" ID ");
//        values.add(" :id ");
//        namedParameters.put("id", UUID.randomUUID());
//
//        if(categoryItem.getCategoryId() != null){
//            into.add(" CATEGORY_ID ");
//            values.add(" :categoryId ");
//            namedParameters.put("categoryId",categoryItem.getCategoryId());
//        }
//
//        if(categoryItem.getItemId() != null){
//            into.add(" ITEM_ID ");
//            values.add(" :itemId ");
//            namedParameters.put("itemId",categoryItem.getItemId());
//        }
//
//        if(categoryItem.getDateAdd() != null){
//            into.add(" DATE_ADD ");
//            values.add(" :dateAdd ");
//            namedParameters.put("dateAdd",categoryItem.getDateAdd());
//        }
//
//        if(values.size()>0){
//            int res = parameterJdbcTemplate.update(" insert into CATEGORY_ITEM ( " + String.join(",", into) + " )" + " values ( " + String.join(",", values) + " )",namedParameters);
//            result = res == 1 ? Boolean.TRUE : Boolean.FALSE;
//        }
//
//        return result;
//    }

}
