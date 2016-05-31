package com.acme.repository;

import com.acme.config.Queue;
import com.acme.exception.PersistException;
import com.acme.model.Category;
import com.acme.repository.mapper.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class CategoryRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate parameterJdbcTemplate;

    public List<Map<String,Object>> getFullMap(){
        return jdbcTemplate.queryForList(Queue.CATEGORY_NAME_MAP);
    }

    public List<Category> getAll(){
        return jdbcTemplate.query(Queue.CATEGORY_FIND_ALL, Mappers.categoryMapper);
    }

    public List<Map<String,Object>> getRootsAsMap(){
        return jdbcTemplate.queryForList(Queue.CATEGORY_FIND_ALL_ROOTS);
    }

    public Category getByID(String id){
        return jdbcTemplate.queryForObject(Queue.CATEGORY_FIND_BY_ID, Mappers.categoryMapper, id);
    }

    public List<Category> getByParentID(String id){
        return jdbcTemplate.query(Queue.CATEGORY_FIND_BY_PARENT_ID, Mappers.categoryMapper, id);
    }

    public List<Category> getByIdList(List<String> idList){
        SqlParameterSource namedParameters = new MapSqlParameterSource("ids", idList);
        return parameterJdbcTemplate.query(Queue.CATEGORY_FIND_BY_ID_LIST,namedParameters, Mappers.categoryMapper);
    }

    public void update(Category category) throws PersistException {
        int result = jdbcTemplate.update(Queue.CATEGORY_UPDATE_BY_ID,category.getName(),category.getParentId(), category.getDateAdd(), category.getId());
        if(result != 1){
            throw new PersistException(String.format(" Can't update Category -> %s", category.toString()));
        }
    }

    public void insert(Category category) throws PersistException {
        category.setId(UUID.randomUUID().toString());
        int result = jdbcTemplate.update(Queue.CATEGORY_INSERT, category.getId(),category.getName(),category.getParentId(),category.getDateAdd());
        if(result != 1){
            throw new PersistException(String.format(" Can't insert new Category -> %s", category.toString()));
        };
    }

    public boolean delete(String id){
        int result = jdbcTemplate.update(Queue.CATEGORY_DELETE,id,id);
        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
    }


}
