package com.acme.repository;

import com.acme.config.Queue;
import com.acme.model.PurchaseOrder;
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
public class PurchaseOrderRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate parameterJdbcTemplate;

    public List<PurchaseOrder> getAll(){
        return jdbcTemplate.query(Queue.PURCHASE_ORDER_FIND_ALL, Mappers.purchaseOrderMapper);
    }

    public PurchaseOrder getById(String id){
        return jdbcTemplate.queryForObject(Queue.PURCHASE_ORDER_FIND_BY_ID, Mappers.purchaseOrderMapper, id);
    }

    public boolean deleteById(String id){
        int result = jdbcTemplate.update(Queue.PURCHASE_ORDER_DELETE_BY_ID,id);
        return result == 1;
    }

    public boolean deleteBySubjectId(String subjectId){
        int result = jdbcTemplate.update(Queue.PURCHASE_ORDER_DELETE_BY_SUBJECT_ID,subjectId);
        return result == 1;
    }

    public List<PurchaseOrder> getBySubjectId(String subjectId){
        return jdbcTemplate.query(Queue.PURCHASE_ORDER_FIND_BY_SUBJECT_ID, Mappers.purchaseOrderMapper,subjectId);
    }

    public boolean insertSelective(PurchaseOrder order){
        Boolean result = Boolean.FALSE;

        Map<String,Object> namedParameters = Maps.newHashMap();
        List<String> into = Lists.newArrayList();
        List<String> values = Lists.newArrayList();

        // for id property
        into.add(" ID ");
        values.add(" :id ");
        namedParameters.put("id", UUID.randomUUID());

        if(order.getSubjectId() != null){
            into.add(" SUBJECT_ID ");
            values.add(" :subjectId ");
            namedParameters.put("subjectId",order.getSubjectId());
        }

        if(order.getCloseOrderDate() != null){
            into.add(" CLOSE_ORDER_DATE ");
            values.add(" :closeOrderDate ");
            namedParameters.put("closeOrderDate",order.getCloseOrderDate());
        }

        if(order.getUid() != null){
            into.add(" UID ");
            values.add(" :uid ");
            namedParameters.put("uid",order.getUid());
        }

        if(order.getRecipientFname() != null){
            into.add(" RECIPIENT_FNAME ");
            values.add(" :fname ");
            namedParameters.put("fname",order.getRecipientFname());
        }

        if(order.getRecipientLname() != null){
            into.add(" RECIPIENT_LNAME ");
            values.add(" :lname ");
            namedParameters.put("lname",order.getRecipientLname());
        }

        if(order.getRecipientMname() != null){
            into.add(" RECIPIENT_MNAME ");
            values.add(" :mname ");
            namedParameters.put("mname",order.getRecipientMname());
        }

        if(order.getRecipientAddress() != null){
            into.add(" RECIPIENT_ADDRESS ");
            values.add(" :recipientAddress ");
            namedParameters.put("recipientAddress",order.getRecipientAddress());
        }

        if(order.getRecipientEmail() != null){
            into.add(" RECIPIENT_EMAIL ");
            values.add(" :recipientEmail ");
            namedParameters.put("recipientEmail",order.getRecipientEmail());
        }

        if(order.getRecipientPhone() != null){
            into.add(" RECIPIENT_PHONE ");
            values.add(" :recipientPhone ");
            namedParameters.put("recipientPhone",order.getRecipientPhone());
        }

        if(order.getComment() != null){
            into.add(" COMMENT ");
            values.add(" :comment ");
            namedParameters.put("comment",order.getComment());
        }

        if(order.getStatus() != null){
            into.add(" STATUS ");
            values.add(" :status ");
            namedParameters.put("status",order.getStatus());
        }

        if(order.getPayment() != null){
            into.add(" PAYMENT ");
            values.add(" :payment ");
            namedParameters.put("payment",order.getPayment());
        }

        if(order.getDelivery() != null){
            into.add(" DELIVERY ");
            values.add(" :delivery ");
            namedParameters.put("delivery",order.getDelivery());
        }

        if(order.getDateAdd() != null){
            into.add(" DATE_ADD ");
            values.add(" :dateAdd ");
            namedParameters.put("dateAdd",order.getDateAdd());
        }

        if(values.size()>0){
            int res = parameterJdbcTemplate.update(" insert into PURCHASE_ORDER ( " + String.join(",", into) + " )" + " values ( " + String.join(",", values) + " )",namedParameters);
            result = res == 1 ? Boolean.TRUE : Boolean.FALSE;
        }

        return result;
    }

    public boolean updateSelectiveById(PurchaseOrder order){
        Map<String,Object> namedParameters = Maps.newHashMap();

        String queue = updateSelective(order,namedParameters);
        int res = parameterJdbcTemplate.update(queue + " WHERE id = "+ order.getId(),namedParameters);
        return res == 1;
    }

    private String updateSelective(PurchaseOrder order, Map<String,Object> namedParameters){
        StringBuilder querySB = new StringBuilder("update PURCHASE_ORDER set");
        if(order.getId() != null){
            querySB.append(" ID = :id ");
            namedParameters.put("id", order.getId());
        }

        if(order.getSubjectId() != null){
            querySB.append(" SUBJECT_ID = :subjectId ");
            namedParameters.put("subjectId",order.getSubjectId());
        }

        if(order.getCloseOrderDate() != null){
            querySB.append(" CLOSE_ORDER_DATE = :closeDate ");
            namedParameters.put("closeDate",order.getCloseOrderDate());
        }

        if(order.getUid() != null){
            querySB.append(" UID = :uid ");
            namedParameters.put("uid",order.getUid());
        }

        if(order.getRecipientFname() != null){
            querySB.append(" RECIPIENT_FNAME = :fname ");
            namedParameters.put("fname",order.getRecipientFname());
        }

        if(order.getRecipientLname() != null){
            querySB.append(" RECIPIENT_LNAME = :lname ");
            namedParameters.put("lname",order.getRecipientLname());
        }

        if(order.getRecipientMname() != null){
            querySB.append(" RECIPIENT_MNAME = :mname ");
            namedParameters.put("mname",order.getRecipientMname());
        }

        if(order.getRecipientAddress() != null){
            querySB.append(" RECIPIENT_ADDRESS = :recipientAddress ");
            namedParameters.put("recipientAddress",order.getRecipientAddress());
        }

        if(order.getRecipientEmail() != null){
            querySB.append(" RECIPIENT_EMAIL = :recipientEmail ");
            namedParameters.put("recipientEmail",order.getRecipientEmail());
        }

        if(order.getRecipientPhone() != null){
            querySB.append(" RECIPIENT_PHONE = :recipientPhone ");
            namedParameters.put("recipientPhone",order.getRecipientPhone());
        }

        if(order.getComment() != null){
            querySB.append(" COMMENT = :comment ");
            namedParameters.put("comment",order.getComment());
        }

        if(order.getStatus() != null){
            querySB.append(" STATUS = :status ");
            namedParameters.put("status",order.getStatus());
        }

        if(order.getPayment() != null){
            querySB.append(" PAYMENT = :payment ");
            namedParameters.put("payment",order.getPayment());
        }

        if(order.getDelivery() != null){
            querySB.append(" DELIVERY = :delivery ");
            namedParameters.put("delivery",order.getDelivery());
        }

        if(order.getDateAdd() != null){
            querySB.append(" DATE_ADD = :dateAdd ");
            namedParameters.put("dateAdd",order.getDateAdd());
        }

        return querySB.toString();
    }


}
