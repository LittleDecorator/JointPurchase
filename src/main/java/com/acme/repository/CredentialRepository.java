package com.acme.repository;

import com.acme.config.Queue;
import com.acme.model.Credential;
import com.acme.repository.mapper.Mappers;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class CredentialRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate parameterJdbcTemplate;

    public Credential getById(String id){
        return jdbcTemplate.queryForObject(Queue.CREDENTIAL_FIND_BY_ID, Mappers.credentialMapper,id);
    }

    public boolean update(Credential credential){
        int result = jdbcTemplate.update(Queue.CREDENTIAL_UPDATE_BY_ID, credential.getPassword(), credential.getRoleId(), credential.getDateAdd(),credential.getSubjectId());
        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean insert(Credential credential){
        Map<String,Object> namedParameters = Maps.newHashMap();
        namedParameters.put("subjectId", credential.getSubjectId());
        namedParameters.put("password", credential.getPassword());
        namedParameters.put("roleId", credential.getRoleId());
        namedParameters.put("dateAdd", credential.getDateAdd());
        int result = parameterJdbcTemplate.update(Queue.CREDENTIAL_INSERT,namedParameters);
        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
    }



}
