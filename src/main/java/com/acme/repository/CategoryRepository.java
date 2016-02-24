package com.acme.repository;

import com.acme.config.Queue;
import com.acme.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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

    public List<Map<String,Object>> getNameMap(){
        return jdbcTemplate.queryForList(Queue.CATEGORY_NAME_MAP);
    }

    public List<Category> getAll(){
        return jdbcTemplate.query(Queue.CATEGORY_FIND_ALL, categoryMapper);
    }

    public Category getByID(String id){
        return jdbcTemplate.queryForObject(Queue.CATEGORY_FIND_BY_ID, categoryMapper, id);
    }

    public List<Category> getByIdList(List<String> idList){
        SqlParameterSource namedParameters = new MapSqlParameterSource("ids", idList);
        return parameterJdbcTemplate.query(Queue.CATEGORY_FIND_BY_ID_LIST,namedParameters,categoryMapper);
    }

    public boolean update(Category category){
        int result = jdbcTemplate.update(Queue.CATEGORY_UPDATE_BY_ID,category.getName(),category.getParentId(), category.getDateAdd(), category.getId());
        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean insert(Category category){
        category.setId(UUID.randomUUID().toString());
        int result = jdbcTemplate.update(Queue.CATEGORY_INSERT, category.getId(),category.getName(),category.getParentId(),category.getDateAdd());
        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean delete(String id){
        int result = jdbcTemplate.update(Queue.CATEGORY_DELETE,id);
        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    private RowMapper<Category> categoryMapper = (rs,num) -> {
        Category category = new Category();
        category.setId(rs.getString("id"));
        category.setName(rs.getString("name"));
        category.setDateAdd(rs.getTimestamp("date_add"));
        category.setParentId(rs.getString("parent_id"));
        return category;
    };
}
