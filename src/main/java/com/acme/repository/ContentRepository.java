package com.acme.repository;

import com.acme.config.Queue;
import com.acme.handlers.Base64BytesSerializer;
import com.acme.model.Content;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class ContentRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate parameterJdbcTemplate;

    public Content getById(String id){
        return jdbcTemplate.queryForObject(Queue.CONTENT_FIND_BY_ID,contentMapper,id);
    }

    public List<Content> getDefault(){
        return jdbcTemplate.query(Queue.CONTENT_FIND_DEFAULT,contentMapper);
    }

    public void deleteByID(String id){
        jdbcTemplate.update(Queue.CONTENT_DELETE_BY_ID,id);
    }

    public boolean insert(Content content) throws IOException {
        content.setId(UUID.randomUUID().toString());
        Map<String,Object> namedParameters = Maps.newHashMap();
        namedParameters.put("id", content.getId());
        namedParameters.put("fileName", content.getFileName());
        namedParameters.put("mime", content.getMime());
        namedParameters.put("type", content.getType());
        namedParameters.put("isDefault", content.isIsDefault()?'Y':'N');
        namedParameters.put("dateAdd", content.getDateAdd());
        namedParameters.put("content", Base64BytesSerializer.serialize(content.getContent()));
        int result = parameterJdbcTemplate.update(Queue.CONTENT_INSERT,namedParameters);
        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    private RowMapper<Content> contentMapper = (rs,num) -> {
        Content content = new Content();
        try {
            content.setId(rs.getString("id"));
            content.setContent(Base64BytesSerializer.deserialize(rs.getString("content")));
            content.setFileName(rs.getString("file_name"));
            content.setMime(rs.getString("mime"));
            content.setType(rs.getString("type"));
            content.setIsDefault(rs.getBoolean("is_default"));
            content.setDateAdd(rs.getDate("date_add"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(content);
        return content;
    } ;


    /*
    *
    * id character varying(128) NOT NULL,
  content text,
  file_name character varying(255),
  mime character varying(25),
  type character varying(25),
  is_default character(1) NOT NULL DEFAULT 'N'::bpchar,
  date_add timestamp without time zone DEFAULT now(),
    *
    * */
}
