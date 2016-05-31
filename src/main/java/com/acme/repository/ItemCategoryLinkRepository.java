package com.acme.repository;

import com.acme.config.Queue;
import com.acme.model.ItemCategoryLink;
import com.acme.repository.mapper.Mappers;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ItemCategoryLinkRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate parameterJdbcTemplate;

    public List<ItemCategoryLink> getGetAll(){
        return jdbcTemplate.query(Queue.ITEM_CATEGORY_LINK_FIND_ITEM_CATEGORIES, Mappers.itemCategoryLinkMapper);
    }

    public List<ItemCategoryLink> getFilteredItems(String name, String article, String company){
        List<ItemCategoryLink> result = Lists.newArrayList();
        List<String> clause = Lists.newArrayList();
        Map<String,Object> namedParameters = Maps.newHashMap();

        if(!Strings.isNullOrEmpty(name)){
            clause.add(" lower(i.name) like :name ");
            namedParameters.put("name","%"+name+"%");
        }

        if(!Strings.isNullOrEmpty(article)){
            clause.add(" i.article like :article ");
            namedParameters.put("article","%"+article+"%");
        }

        if(!Strings.isNullOrEmpty(company)){
            clause.add(" i.company_id = ':company' ");
            namedParameters.put("company",company);
        }

        if(namedParameters.size()>0){
            int pos = Queue.ITEM_CATEGORY_LINK_FIND_FILTERED_ITEMS.indexOf("ORDER");
            if(pos != -1){
                String firstPart = Queue.ITEM_CATEGORY_LINK_FIND_FILTERED_ITEMS.substring(0,pos);
                String secondPart = Queue.ITEM_CATEGORY_LINK_FIND_FILTERED_ITEMS.substring(pos);
                String queue = firstPart + " WHERE " + String.join(" AND ",clause) + secondPart;
                result = parameterJdbcTemplate.query(queue,namedParameters, Mappers.itemCategoryLinkMapper);
            }
        } else {
            result = jdbcTemplate.query(Queue.ITEM_CATEGORY_LINK_FIND_FILTERED_ITEMS, Mappers.itemCategoryLinkMapper);
        }
        return result;
    }

}
