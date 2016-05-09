package com.acme.repository;

import com.acme.config.Queue;
import com.acme.model.Item;
import com.acme.model.Subject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Repository
public class SubjectRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate parameterJdbcTemplate;

    public List<Subject> getAll(){
        return jdbcTemplate.query(Queue.SUBJECT_FIND_ALL, subjectMapper);
    }

    public Subject getByEmail(String login){
        return jdbcTemplate.queryForObject(Queue.SUBJECT_FIND_BY_EMAIL, subjectMapper, login);
    }

    public Subject getById(String id){
        return jdbcTemplate.queryForObject(Queue.SUBJECT_FIND_BY_ID,subjectMapper,id);
    }

    public boolean deleteById(String id){
        int result = jdbcTemplate.update(Queue.SUBJECT_DELETE_BY_ID,id);
        return result == 1;
    }

    public boolean updateSelectiveById(Subject subject){
        Map<String,Object> namedParameters = Maps.newHashMap();

        String queue = updateSelective(subject,namedParameters);
        int res = parameterJdbcTemplate.update(queue + " WHERE id = "+ subject.getId(), namedParameters);
        return res == 1;
    }

    private String updateSelective(Subject subject, Map<String,Object> namedParameters){
        StringBuilder querySB = new StringBuilder("update SUBJECT set");

        querySB.append(" ENABLED = :enabled ");
        namedParameters.put("enabled", subject.isEnabled());

        if(subject.getId() != null){
            querySB.append(" ID = :id ");
            namedParameters.put("id", subject.getId());
        }

        if(subject.getFirstName() != null){
            querySB.append(" FIRST_NAME = :firstName ");
            namedParameters.put("firstName",subject.getFirstName());
        }

        if (subject.getMiddleName() != null){
            querySB.append(" MIDDLE_NAME = :middleName ");
            namedParameters.put("middleName",subject.getMiddleName());
        }

        if(subject.getLastName() != null){
            querySB.append(" LAST_NAME = :lastName ");
            namedParameters.put("lastName",subject.getLastName());
        }

        if (subject.getPhoneNumber() != null){
            querySB.append(" PHONE_NUMBER = :phoneNumber ");
            namedParameters.put("phoneNumber",subject.getPhoneNumber());
        }

        if(subject.getEmail() != null){
            querySB.append(" EMAIL = :email ");
            namedParameters.put("email",subject.getEmail());
        }

        if(subject.getAddress() != null){
            querySB.append(" ADDRESS = :address ");
            namedParameters.put("address",subject.getAddress());
        }

        if(subject.getPostAddress() != null){
            querySB.append(" POST_ADDRESS = :postAddress ");
            namedParameters.put("postAddress",subject.getPostAddress());
        }

        if(subject.getDateAdd() != null){
            querySB.append(" DATE_ADD = :dateAdd ");
            namedParameters.put("dateAdd",subject.getDateAdd());
        }

        return querySB.toString();
    }

    public boolean insertSelective(Subject subject){
        Boolean result = Boolean.FALSE;

        Map<String,Object> namedParameters = Maps.newHashMap();
        List<String> into = Lists.newArrayList();
        List<String> values = Lists.newArrayList();

        // for id property
        into.add(" ID ");
        values.add(" :id ");
        namedParameters.put("id", UUID.randomUUID());

        into.add(" ENABLED ");
        values.add(" :enabled ");
        namedParameters.put("enabled", subject.isEnabled());

        if(subject.getFirstName() != null){
            into.add(" FIRST_NAME ");
            values.add(" :firstName ");
            namedParameters.put("firstName", subject.getFirstName());
        }

        if(subject.getMiddleName() != null){
            into.add(" MIDDLE_NAME ");
            values.add(" :middleName ");
            namedParameters.put("middleName",subject.getMiddleName());
        }

        if(subject.getLastName() != null){
            into.add(" LAST_NAME ");
            values.add(" :lastName ");
            namedParameters.put("lastName",subject.getLastName());
        }

        if(subject.getPhoneNumber() != null){
            into.add(" PHONE_NUMBER ");
            values.add(" :phoneNumber ");
            namedParameters.put("phoneNumber",subject.getPhoneNumber());
        }

        if(subject.getEmail() != null){
            into.add(" EMAIL ");
            values.add(" :email ");
            namedParameters.put("email",subject.getEmail());
        }

        if(subject.getAddress() != null){
            into.add(" ADDRESS ");
            values.add(" :address ");
            namedParameters.put("address",subject.getAddress());
        }

        if(subject.getPostAddress() != null){
            into.add(" POST_ADDRESS ");
            values.add(" :postAddress ");
            namedParameters.put("postAddress",subject.getPostAddress());
        }

        if(subject.getDateAdd() != null){
            into.add(" DATE_ADD ");
            values.add(" :dateAdd ");
            namedParameters.put("dateAdd",subject.getDateAdd());
        }

        if(values.size()>0){
            int res = parameterJdbcTemplate.update(" insert into SUBJECT ( " + String.join(",", into) + " )" + " values ( " + String.join(",", values) + " )",namedParameters);
            result = res == 1 ? Boolean.TRUE : Boolean.FALSE;
        }

        return result;
    }

    public boolean insert(Subject subject){
        subject.setId(UUID.randomUUID().toString());
        Map<String,Object> namedParameters = Maps.newHashMap();
        namedParameters.put("id", subject.getId());
        namedParameters.put("enabled", subject.isEnabled());
        namedParameters.put("firstName", subject.getFirstName());
        namedParameters.put("middleName", subject.getMiddleName());
        namedParameters.put("lastName", subject.getLastName());
        namedParameters.put("phoneNumber", subject.getPhoneNumber());
        namedParameters.put("email", subject.getEmail());
        namedParameters.put("address", subject.getAddress());
        namedParameters.put("postAddress", subject.getPostAddress());
        namedParameters.put("dateAdd", subject.getDateAdd());
        int result = parameterJdbcTemplate.update(Queue.SUBJECT_INSERT,namedParameters);
        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    private RowMapper<Subject> subjectMapper = (rs,num) -> {
        Subject subject = new Subject();
        subject.setId(rs.getString("id"));
        subject.setEnabled(Objects.equals(rs.getString("enabled"), "Y"));
        subject.setFirstName(rs.getString("first_name"));
        subject.setMiddleName(rs.getString("middle_name"));
        subject.setLastName(rs.getString("last_name"));
        subject.setPhoneNumber(rs.getString("phone_number"));
        subject.setEmail(rs.getString("email"));
        subject.setAddress(rs.getString("address"));
        subject.setPhoneNumber(rs.getString("post_address"));
        subject.setDateAdd(rs.getDate("date_add"));
        return subject;
    };

}
