package com.acme.repository;

import com.acme.constant.Queue;
import com.acme.model.PurchaseOrder;
import com.acme.model.dto.OrderView;
import com.acme.model.filter.OrderFilter;
import com.acme.repository.mapper.Mappers;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class PurchaseOrderRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate parameterJdbcTemplate;

    public List<OrderView> getAll(OrderFilter filter){

        List<String> queryParams = Lists.newArrayList();
        List<Object> parameters = Lists.newArrayList();

        if(filter.getDateFrom() != null){
            queryParams.add("create_order_date >= ?");
            parameters.add(Date.from(filter.getDateFrom().atZone(ZoneId.of("UTC")).toInstant()));
        }

        if(filter.getDateTo() != null){
            queryParams.add("create_order_date <= ?");
            parameters.add(Date.from(filter.getDateTo().atZone(ZoneId.of("UTC")).toInstant()));
        }

        if(filter.getStatus() != null){
            queryParams.add("status = ?");
            parameters.add(filter.getStatus());
        }

        if(filter.getSubjectId() != null){
            queryParams.add("recipient_id = ?");
            parameters.add(filter.getSubjectId());
        }

        String queue = Queue.PURCHASE_ORDER_FIND_ALL;
        if(!queryParams.isEmpty()){
            queue += " WHERE "+ String.join(" AND ", queryParams);
        }
        queue += " order by create_order_date desc offset "+ filter.getOffset() + " limit "+ filter.getLimit();
        System.out.println(parameters);
        return jdbcTemplate.query(queue , parameters.toArray(), Mappers.orderViewMapper);
    }

    public PurchaseOrder getById(String id){
        System.out.println(id);
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

    public List<OrderView> getBySubjectId(String subjectId){
        return jdbcTemplate.query(Queue.PURCHASE_ORDER_FIND_BY_SUBJECT_ID, Mappers.orderViewMapper,subjectId);
    }

    public boolean insertSelective(PurchaseOrder order){
        Boolean result = Boolean.FALSE;

        Map<String,Object> namedParameters = Maps.newHashMap();
        List<String> into = Lists.newArrayList();
        List<String> values = Lists.newArrayList();

        // for id property
        into.add("ID");
        values.add(":id");
        order.setId(UUID.randomUUID().toString());
        namedParameters.put("id", order.getId());


        if(order.getSubjectId() != null){
            into.add("SUBJECT_ID");
            values.add(":subjectId");
            namedParameters.put("subjectId",order.getSubjectId());
        }

        if(order.getCloseOrderDate() != null){
            into.add("CLOSE_ORDER_DATE");
            values.add(":closeOrderDate");
            namedParameters.put("closeOrderDate",order.getCloseOrderDate());
        }

        if(order.getUid() != null){
            into.add("UID");
            values.add(":uid");
            namedParameters.put("uid",order.getUid());
        }

        if(order.getRecipientFname() != null){
            into.add("RECIPIENT_FNAME");
            values.add(":fname");
            namedParameters.put("fname",order.getRecipientFname());
        }

        if(order.getRecipientLname() != null){
            into.add("RECIPIENT_LNAME");
            values.add(":lname");
            namedParameters.put("lname",order.getRecipientLname());
        }

        if(order.getRecipientMname() != null){
            into.add("RECIPIENT_MNAME");
            values.add(":mname");
            namedParameters.put("mname",order.getRecipientMname());
        }

        if(order.getRecipientAddress() != null){
            into.add("RECIPIENT_ADDRESS");
            values.add(":recipientAddress");
            namedParameters.put("recipientAddress",order.getRecipientAddress());
        }

        if(order.getRecipientEmail() != null){
            into.add("RECIPIENT_EMAIL");
            values.add(":recipientEmail");
            namedParameters.put("recipientEmail",order.getRecipientEmail());
        }

        if(order.getRecipientPhone() != null){
            into.add("RECIPIENT_PHONE");
            values.add(":recipientPhone");
            namedParameters.put("recipientPhone",order.getRecipientPhone());
        }

        if(order.getComment() != null){
            into.add("COMMENT");
            values.add(":comment");
            namedParameters.put("comment",order.getComment());
        }

        if(order.getStatus() != null){
            into.add("STATUS");
            values.add(":status");
            namedParameters.put("status",CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, order.getStatus().name()));
        }

        if(order.getPayment() != null){
            into.add("PAYMENT");
            values.add(":payment");
            namedParameters.put("payment",order.getPayment());
        }

        if(order.getDelivery() != null){
            into.add("DELIVERY_ID");
            values.add(":delivery");
            namedParameters.put("delivery",order.getDelivery());
        }

        if(order.getDateAdd() != null){
            into.add("DATE_ADD");
            values.add(":dateAdd");
            namedParameters.put("dateAdd",order.getDateAdd());
        }

        if(values.size()>0){
            String sql = " insert into public.purchase_order ( " + String.join(",", into) + " ) values ( " + String.join(",", values) + " )";
            System.out.println(sql);
            int res = parameterJdbcTemplate.update(sql,namedParameters);
            result = res == 1 ? Boolean.TRUE : Boolean.FALSE;
            System.out.println(result);
        }

        return result;
    }

    public boolean updateSelectiveById(PurchaseOrder order){
        Map<String,Object> namedParameters = Maps.newHashMap();
        String queue = updateSelective(order,namedParameters);
        int res = parameterJdbcTemplate.update(queue + " WHERE id = :id",namedParameters);
        return res == 1;
    }

    private String updateSelective(PurchaseOrder order, Map<String,Object> namedParameters){
        List<String> query = Lists.newArrayList();
        namedParameters.put("id", order.getId());

        if(order.getSubjectId() != null){
            query.add(" subject_id = :subjectId ");
            namedParameters.put("subjectId",order.getSubjectId());
        }

        if(order.getCloseOrderDate() != null){
            query.add(" close_order_date = :closeDate ");
            namedParameters.put("closeDate",order.getCloseOrderDate());
        }

//        if(order.getUid() != null){
//            query.add(" UID = :uid ");
//            namedParameters.put("uid",order.getUid());
//        }

        if(order.getRecipientFname() != null){
            query.add(" recipient_fname = :fname ");
            namedParameters.put("fname",order.getRecipientFname());
        }

        if(order.getRecipientLname() != null){
            query.add(" recipient_lname = :lname ");
            namedParameters.put("lname",order.getRecipientLname());
        }

        if(order.getRecipientMname() != null){
            query.add(" recipient_mname = :mname ");
            namedParameters.put("mname",order.getRecipientMname());
        }

        if(order.getRecipientAddress() != null){
            query.add(" recipient_address = :recipientAddress ");
            namedParameters.put("recipientAddress",order.getRecipientAddress());
        }

        if(order.getRecipientEmail() != null){
            query.add(" recipient_email = :recipientEmail ");
            namedParameters.put("recipientEmail",order.getRecipientEmail());
        }

        if(order.getRecipientPhone() != null){
            query.add(" recipient_phone = :recipientPhone ");
            namedParameters.put("recipientPhone",order.getRecipientPhone());
        }

        if(order.getComment() != null){
            query.add(" comment = :comment ");
            namedParameters.put("comment",order.getComment());
        }

        if(order.getStatus() != null){
            query.add(" status = :status ");
            namedParameters.put("status", CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, order.getStatus().name()));
        }

        if(order.getPayment() != null){
            query.add(" payment = :payment ");
            namedParameters.put("payment",order.getPayment());
        }

        if(order.getDelivery() != null){
            query.add(" delivery_id = :delivery ");
            namedParameters.put("delivery",order.getDelivery());
        }

//        if(order.getDateAdd() != null){
//            query.add(" DATE_ADD = :dateAdd ");
//            namedParameters.put("dateAdd",order.getDateAdd());
//        }

        return "update public.purchase_order set" + String.join(",",query);
    }


}
