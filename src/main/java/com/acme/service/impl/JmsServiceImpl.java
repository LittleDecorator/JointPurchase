package com.acme.service.impl;

import com.acme.model.Order;
import com.acme.model.Subject;
import com.acme.service.JmsService;
import com.google.api.client.util.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by nikolay on 13.08.17.
 */

@Service
public class JmsServiceImpl implements JmsService{

    @Autowired
    JmsTemplate jmsTemplate;

    @Override
    public void registrationRequest(Subject subject, String recipient, String token) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("recipient", recipient);
        params.put("token", token);
        params.put("subject", subject);
        jmsTemplate.convertAndSend("registration_request", params);
    }

    @Override
    public void registrationConfirm(Subject subject) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("subject", subject);
        jmsTemplate.convertAndSend("registration_confirm", params);
    }

    @Override
    public void passwordChangeRequest(Subject subject, String token) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("token", token);
        params.put("subject", subject);
        jmsTemplate.convertAndSend("password_change_request", params);
    }

    @Override
    public void orderConfirm(Order order) {
        jmsTemplate.convertAndSend("order_confirm", order);
    }
}
