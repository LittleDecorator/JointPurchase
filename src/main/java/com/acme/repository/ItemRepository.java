package com.acme.repository;

import com.acme.config.Queue;
import com.acme.model.Item;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
public class ItemRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate parameterJdbcTemplate;

    public List<Item> getAll(){
        return jdbcTemplate.query(Queue.ITEM_FIND_ALL,itemMapper);
    }

    public List<Item> getByCompanyForSale(String companyId){
        return jdbcTemplate.query(Queue.ITEM_BY_COMPANY_FOR_SALE,itemMapper,companyId);
    }

    public List<Item> getByCompanyId(String companyId){
        return jdbcTemplate.query(Queue.ITEM_BY_COMPANY_ID,itemMapper,companyId);
    }

    public Item getById(String id){
        return jdbcTemplate.queryForObject(Queue.ITEM_FIND_BY_ID, itemMapper, id);
    }

    public List<Item> getByIdList(List<String> ids){
        SqlParameterSource parameterSource = new MapSqlParameterSource("idList",ids);
        return parameterJdbcTemplate.query(Queue.ITEM_FIND_BY_ID_LIST, parameterSource, itemMapper);
    }

    public List<Item> getBySearch(String criteria){
        Map<String,Object> namedParameters = Maps.newHashMap();
        namedParameters.put("criteria", "%"+criteria+"%");
        return parameterJdbcTemplate.query(Queue.ITEM_FIND_BY_SEARCH,namedParameters,itemMapper);
    }

    public boolean deleteById(String id){
        int result = jdbcTemplate.update(Queue.ITEM_DELETE_BY_ID,id);
        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean updateSelectiveById(Item item){
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String queue = updateSelective(item,parameters);
        parameters.addValue("id",item.getId());
        int res = parameterJdbcTemplate.update(queue + " WHERE id = :id", parameters);
        return res == 1;
    }

    public boolean insertSelective(Item item){
        Boolean result = Boolean.FALSE;

        Map<String,Object> namedParameters = Maps.newHashMap();
        List<String> into = Lists.newArrayList();
        List<String> values = Lists.newArrayList();

        // for id property
        into.add(" ID ");
        values.add(" :id ");
        namedParameters.put("id", UUID.randomUUID());

        if(item.getName() != null){
            into.add(" NAME ");
            values.add(" :name ");
            namedParameters.put("name",item.getName());
        }

        if(item.getCompanyId() != null){
            into.add(" COMPANY_ID ");
            values.add(" :companyId ");
            namedParameters.put("companyId",item.getCompanyId());
        }

        if(item.getArticle() != null){
            into.add(" ARTICLE ");
            values.add(" :article ");
            namedParameters.put("article",item.getArticle());
        }

        if(item.getDescription() != null){
            into.add(" DESCRIPTION ");
            values.add(" :description ");
            namedParameters.put("description",item.getDescription());
        }

        if(item.getPrice() != null){
            into.add(" PRICE ");
            values.add(" :price ");
            namedParameters.put("price",item.getPrice());
        }

        into.add(" NOT_FOR_SALE ");
        values.add(" :forSale ");
        namedParameters.put("forSale",item.isNotForSale());

        if(item.getInStock() != null){
            into.add(" IN_STOCK ");
            values.add(" :inStock ");
            namedParameters.put("inStock",item.getInStock());
        }

        if(item.getDateAdd() != null){
            into.add(" DATE_ADD ");
            values.add(" :dateAdd ");
            namedParameters.put("dateAdd",item.getDateAdd());
        }

        if(values.size()>0){
            int res = parameterJdbcTemplate.update(" insert into ITEM ( " + String.join(",", into) + " )" + " values ( " + String.join(",", values) + " )",namedParameters);
            result = res == 1 ? Boolean.TRUE : Boolean.FALSE;
        }

        return result;
    }

    private String updateSelective(Item item, MapSqlParameterSource namedParameters){
        List<String> querySB = Lists.newArrayList();

//        if(item.getId() != null){
//            querySB.add(" ID = :id ");
//            namedParameters.put("id", item.getId());
//        }

        if(item.getName() != null){
            querySB.add(" NAME = :name ");
            namedParameters.addValue("name", item.getName());
        }

        if(item.getCompanyId() != null){
            querySB.add(" COMPANY_ID = :companyId ");
            namedParameters.addValue("companyId", item.getCompanyId());
        }

        if(item.getArticle() != null){
            querySB.add(" ARTICLE = :article ");
            namedParameters.addValue("article", item.getArticle());
        }

        if(item.getDescription() != null){
            querySB.add(" DESCRIPTION = :description ");
            namedParameters.addValue("description", item.getDescription());
        }

        if(item.getPrice() != null){
            querySB.add(" PRICE = :price ");
            namedParameters.addValue("price", item.getPrice());
        }

        querySB.add(" NOT_FOR_SALE = :forSale ");
        namedParameters.addValue("forSale", item.isNotForSale()?'Y':'N');

        if(item.getInStock() != null){
            querySB.add(" IN_STOCK = :inStock ");
            namedParameters.addValue("inStock", item.getInStock());
        }

        if(item.getDateAdd() != null){
            querySB.add(" DATE_ADD = :dateAdd ");
            namedParameters.addValue("dateAdd", item.getDateAdd());
        }

        return "update ITEM set " + String.join(",",querySB);
    }

    private RowMapper<Item> itemMapper = (rs,num) -> {
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
        return item;
    };

}
