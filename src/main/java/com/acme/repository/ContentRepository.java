package com.acme.repository;

import com.acme.config.Queue;
import com.acme.handlers.Base64BytesSerializer;
import com.acme.model.Content;
import com.acme.repository.mapper.Mappers;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@CacheConfig(cacheNames = "content")
public class ContentRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate parameterJdbcTemplate;

    @Cacheable
    public Content getById(String id){
        return jdbcTemplate.queryForObject(Queue.CONTENT_FIND_BY_ID,Mappers.contentMapper,id);
    }

    public List<Content> getDefault(){
        return jdbcTemplate.query(Queue.CONTENT_FIND_DEFAULT, Mappers.contentMapper);
    }

    public void deleteByID(String id){
        jdbcTemplate.update(Queue.CONTENT_DELETE_BY_ID,id);
    }

    public boolean insert(Content content) throws IOException {
        content.setId(UUID.randomUUID().toString());
        Map<String, Object> namedParameters = Maps.newHashMap();
        namedParameters.put("id", content.getId());
        namedParameters.put("fileName", content.getFileName());
        namedParameters.put("mime", content.getMime());
        namedParameters.put("type", content.getType());
        namedParameters.put("isDefault", content.isIsDefault() ? 'Y' : 'N');
        namedParameters.put("dateAdd", content.getDateAdd());
        namedParameters.put("content", Base64BytesSerializer.serialize(content.getContent()));
        int result = parameterJdbcTemplate.update(Queue.CONTENT_INSERT, namedParameters);
        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
    }
}
