package com.acme.repository;

import com.acme.constant.Queue;
import com.acme.model.Item;
import com.acme.model.ItemContent;
import com.acme.repository.mapper.Mappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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

    public List<ItemContent> getMain (){
        return jdbcTemplate.query(Queue.ITEM_CONTENT_FIND_MAIN, Mappers.itemContentMapper);
    }

    public int countByItemId(String itemId){
        return jdbcTemplate.queryForObject(Queue.ITEM_CONTENT_COUNT_BY_ITEM_ID, new Object[]{itemId}, Integer.class);
    }

    public List<ItemContent> getByItemId (String itemId){
        return jdbcTemplate.query(Queue.ITEM_CONTENT_FIND_BY_ITEM_ID, Mappers.itemContentMapper, itemId);
    }

    public List<ItemContent> getShowedByItemId (String itemId){
        return jdbcTemplate.query(Queue.ITEM_CONTENT_SHOW_BY_ITEM, Mappers.itemContentMapper, itemId);
    }

    public boolean deleteByContentIdAndItemId(String contentId,String itemId){
        int result = jdbcTemplate.update(Queue.ITEM_CONTENT_DELETE_BY_ITEM_ID_AND_CONTENT_ID, itemId, contentId);
        return result == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    public boolean deleteByItemId(String itemId){
        int result = jdbcTemplate.update(Queue.ITEM_CONTENT_DELETE_BY_ITEM_ID, itemId);
        return result == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    public ItemContent getByItemIdAndImageId(String contentId,String itemId){
        return jdbcTemplate.queryForObject(Queue.ITEM_CONTENT_FIND_BY_ITEM_ID_AND_IMAGE_ID, Mappers.itemContentMapper, itemId, contentId);
    }

    public boolean updateSelective(ItemContent itemContent, Map<String,Object> whereClause){
        Map<String,Object> namedParameters = Maps.newHashMap();
        Boolean result = Boolean.FALSE;

        List<String> querySB = Lists.newArrayList();
        if(itemContent.getId() != null){
            querySB.add(" ID = :id ");
            namedParameters.put("id",itemContent.getId());
        }
        if(itemContent.getItem().getId() != null){
            querySB.add(" ITEM_ID = :itemId ");
            namedParameters.put("itemId",itemContent.getItem().getId());
        }
        if(itemContent.getContent().getId() != null){
            querySB.add(" CONTENT_ID = :contentId ");
            namedParameters.put("contentId",itemContent.getContent().getId());
        }
        if(itemContent.isShow()){
            querySB.add(" SHOW = :show");
            namedParameters.put("show",itemContent.isShow());
        }
        if(itemContent.isMain()){
            querySB.add(" MAIN = :main");
            namedParameters.put("main",itemContent.isMain());
        }
        if(itemContent.getDateAdd() != null){
            querySB.add(" DATE_ADD = :dateAdd");
            namedParameters.put("dateAdd",itemContent.getDateAdd());
        }
        if(itemContent.getCropId() != null){
            querySB.add(" CROP_ID = :cropId");
            namedParameters.put("cropId",itemContent.getCropId());
        }

        String query = "update ITEM_CONTENT set" + String.join(",",querySB);
        StringBuilder whereBuilder = new StringBuilder();

        if(namedParameters.size() > 0 && whereClause.size()>0){
            whereBuilder.append(" where ");
            List<String> keys = whereClause.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
            IntStream.range(0,keys.size())
                    .forEach(idx -> {
                                String key = keys.get(idx);
                                Object value = whereClause.get(key);
                                whereBuilder.append(keys.get(idx)).append(" = ").append(value);
                                if(idx != keys.size()-1){
                                    whereBuilder.append(" AND ");
                                }
                            }
                    );
            int res = parameterJdbcTemplate.update(query + whereBuilder.toString(),namedParameters);
            result = res == 1 ? Boolean.TRUE : Boolean.FALSE;
        }

        return result;
    }

    public boolean updateSelectiveById(ItemContent itemContent){
        Map<String,Object> parameters = Maps.newHashMap();
//        parameters.put("id",itemContent.getId());
        parameters.put("id", ":id");
        return updateSelective(itemContent, parameters);
    }

    public boolean insert(ItemContent itemContent){
//        itemContent.setId(UUID.randomUUID().toString());
        Map<String,Object> namedParameters = Maps.newHashMap();
        namedParameters.put("id", itemContent.getId());
//        namedParameters.put("itemId", itemContent.getItemId());
//        namedParameters.put("contentId", itemContent.getContentId());
        namedParameters.put("show", itemContent.isShow()?'Y':'N');
        namedParameters.put("main", itemContent.isMain()?'Y':'N');
        namedParameters.put("dateAdd", itemContent.getDateAdd());
        int result = parameterJdbcTemplate.update(Queue.ITEM_CONTENT_INSERT,namedParameters);
        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

}
