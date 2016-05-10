package com.acme.repository;

import com.acme.config.Queue;
import com.acme.model.ItemContent;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
public class ItemContentRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate parameterJdbcTemplate;

    public int countByItemId(String itemId){
        return jdbcTemplate.queryForObject(Queue.ITEM_CONTENT_COUNT_BY_ITEM_ID, new Object[]{itemId}, Integer.class);
    }

    public List<ItemContent> getByItemId (String itemId){
        return jdbcTemplate.query(Queue.ITEM_CONTENT_FIND_BY_ITEM_ID, itemContentMapper, itemId);
    }

    public List<ItemContent> getShowedByItemId (String itemId){
        return jdbcTemplate.query(Queue.ITEM_CONTENT_SHOW_BY_ITEM, itemContentMapper, itemId);
    }

    public boolean deleteByContentIdAndItemId(String contentId,String itemId){
        int result = jdbcTemplate.update(Queue.ITEM_CONTENT_DELETE_BY_ITEM_ID_AND_CONTENT_ID, itemId, contentId);
        return result == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    public boolean updateSelective(ItemContent itemContent, Map<String,Object> whereClause){
        Map<String,Object> namedParameters = Maps.newHashMap();
        Boolean result = Boolean.FALSE;

        StringBuilder querySB = new StringBuilder("update ITEM_CONTENT set");
        if(itemContent.getId() != null){
            querySB.append(" ID = :id ");
            namedParameters.put("id",itemContent.getId());
        }
        if(itemContent.getItemId() != null){
            querySB.append(" ITEM_ID = :itemId ");
            namedParameters.put("itemId",itemContent.getItemId());
        }
        if(itemContent.getItemId() != null){
            querySB.append(" CONTENT_ID = :contentId ");
            namedParameters.put("contentId",itemContent.getContentId());
        }
        if(itemContent.getItemId() != null){
            querySB.append(" SHOW = :show");
            namedParameters.put("show",itemContent.isShow());
        }
        if(itemContent.getItemId() != null){
            querySB.append(" MAIN = :main");
            namedParameters.put("main",itemContent.isMain());
        }
        if(itemContent.getItemId() != null){
            querySB.append(" DATE_ADD = :dateAdd");
            namedParameters.put("dateAdd",itemContent.getDateAdd());
        }

        if(namedParameters.size() > 0 && whereClause.size()>0){
            querySB.append(" where ");
            List<String> keys = whereClause.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
            IntStream.range(0,keys.size())
                    .forEach(idx -> {
                                String key = keys.get(idx);
                                Object value = whereClause.get(key);
                                querySB.append(keys.get(idx)).append(" = ").append(value);
                                if(idx != keys.size()){
                                    querySB.append(" AND ");
                                }
                            }
                    );
            int res = parameterJdbcTemplate.update(querySB.toString(),namedParameters);
            result = res == 1 ? Boolean.TRUE : Boolean.FALSE;
        }

        return result;
    }

    public boolean insert(ItemContent itemContent){
        itemContent.setId(UUID.randomUUID().toString());
        Map<String,Object> namedParameters = Maps.newHashMap();
        namedParameters.put("id", itemContent.getId());
        namedParameters.put("itemId", itemContent.getItemId());
        namedParameters.put("contentId", itemContent.getContentId());
        namedParameters.put("show", itemContent.isShow()?'Y':'N');
        namedParameters.put("main", itemContent.isMain()?'Y':'N');
        namedParameters.put("dateAdd", itemContent.getDateAdd());
        int result = parameterJdbcTemplate.update(Queue.ITEM_CONTENT_INSERT,namedParameters);
        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    private RowMapper<ItemContent> itemContentMapper = (rs, num) -> {
        ItemContent itemContent = new ItemContent();
        itemContent.setId(rs.getString("id"));
        itemContent.setItemId(rs.getString("item_id"));
        itemContent.setContentId(rs.getString("content_id"));
        itemContent.setShow(rs.getBoolean("show"));
        itemContent.setMain(rs.getBoolean("main"));
        itemContent.setDateAdd(rs.getDate("date_add"));
        return itemContent;
    };


    /*
    *
    * id character varying(37) NOT NULL,
  item_id character varying(37) NOT NULL,
  content_id character varying(37) NOT NULL,
  show character(1) NOT NULL DEFAULT 'N'::bpchar,
  main character(1) NOT NULL DEFAULT 'N'::bpchar,
  date_add timestamp without time zone DEFAULT now(),
    *
    *
    * */
}
