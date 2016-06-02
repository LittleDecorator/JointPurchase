package com.acme.repository;

import com.acme.constant.Queue;
import com.acme.model.Company;
import com.acme.repository.mapper.Mappers;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class CompanyRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate parameterJdbcTemplate;

    public List<Company> getAll(){
        return jdbcTemplate.query(Queue.COMPANY_FIND_ALL,Mappers.companyMapper);
    }

    public Company getById(String id){
        return jdbcTemplate.queryForObject(Queue.COMPANY_FIND_BY_ID, Mappers.companyMapper, id);
    }

    public boolean deleteById(String id){
        int result = jdbcTemplate.update(Queue.COMPANY_DELETE_BY_ID,id);
        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean insert(Company company){
        company.setId(UUID.randomUUID().toString());
        Map<String,Object> namedParameters = Maps.newHashMap();
        namedParameters.put("id", company.getId());
        namedParameters.put("name", company.getName());
        namedParameters.put("description", company.getDescription());
        namedParameters.put("address", company.getAddress());
        namedParameters.put("email", company.getEmail());
        namedParameters.put("phone", company.getPhone());
        namedParameters.put("url", company.getUrl());
        namedParameters.put("bik", company.getBik());
        namedParameters.put("inn", company.getInn());
        namedParameters.put("ks", company.getKs());
        namedParameters.put("rs", company.getRs());
        namedParameters.put("dateAd", company.getDateAdd());
        int result = parameterJdbcTemplate.update(Queue.SUBJECT_INSERT,namedParameters);
        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
    }



}
