package com.acme.repository;

import com.acme.config.Queue;
import com.acme.model.OrderItem;
import com.acme.repository.mapper.Mappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class OrderItemRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate parameterJdbcTemplate;

    public List<OrderItem> getByOrderId(String orderId){
        return jdbcTemplate.query(Queue.ORDER_ITEM_FIND_BY_ORDER_ID, Mappers.orderItemMapper,orderId);
    }

    public boolean deleteByItemId(String itemId){
        int result = jdbcTemplate.update(Queue.ORDER_ITEM_DELETE_BY_ITEM_ID,itemId);
        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean deleteByOrderId(String orderId){
        int result = jdbcTemplate.update(Queue.ORDER_ITEM_DELETE_BY_ORDER_ID,orderId);
        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean insertSelective(OrderItem orderItem){
        Boolean result = Boolean.FALSE;

        Map<String,Object> namedParameters = Maps.newHashMap();
        List<String> into = Lists.newArrayList();
        List<String> values = Lists.newArrayList();

        // for id property
        into.add(" ID ");
        values.add(" :id ");
        namedParameters.put("id", UUID.randomUUID());

        if(orderItem.getItemId() != null){
            into.add(" ITEM_ID ");
            values.add(" :itemId ");
            namedParameters.put("itemId",orderItem.getItemId());
        }

        if(orderItem.getOrderId() != null){
            into.add(" ORDER_ID ");
            values.add(" :orderId ");
            namedParameters.put("orderId",orderItem.getOrderId());
        }

        if(orderItem.getCou() != null){
            into.add(" COU ");
            values.add(" :cou ");
            namedParameters.put("cou",orderItem.getCou());
        }

        if(orderItem.getDateAdd() != null){
            into.add(" DATE_ADD ");
            values.add(" :dateAdd ");
            namedParameters.put("dateAdd",orderItem.getDateAdd());
        }

        if(values.size()>0){
            int res = parameterJdbcTemplate.update(" insert into ORDER_ITEM ( " + String.join(",", into) + " )" + " values ( " + String.join(",", values) + " )",namedParameters);
            result = res == 1 ? Boolean.TRUE : Boolean.FALSE;
        }

        return result;
    }

}
