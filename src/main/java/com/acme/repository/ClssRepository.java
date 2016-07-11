package com.acme.repository;

import com.acme.constant.Queue;
import com.acme.model.Delivery;
import com.acme.repository.mapper.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClssRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate parameterJdbcTemplate;

    public List<Delivery> getDelivery(){
        return jdbcTemplate.query(Queue.DELIVERY_GET_ALL, Mappers.deliveryMapper);
    }

}
