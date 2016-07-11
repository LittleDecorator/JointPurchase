package com.acme.controller;

import com.acme.enums.OrderStatus;
import com.acme.model.Delivery;
import com.acme.repository.ClssRepository;
import com.google.common.base.CaseFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clss")
public class ClssController {

    @Autowired
    ClssRepository clssRepository;

    @RequestMapping(method = RequestMethod.GET,value = "/order/status/map")
    public Map<String,String> getOrderStatus(){
        return Arrays.stream(OrderStatus.values()).collect(Collectors.toMap( e -> CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, e.name()),OrderStatus::getText));
    }

    @RequestMapping(method = RequestMethod.GET,value = "/order/delivery")
    public List<Delivery> getDelivery(){
        return clssRepository.getDelivery();
    }

    @RequestMapping(method = RequestMethod.GET,value = "/order/delivery/map")
    public Map<String,String> getDeliveryMap(){
        return clssRepository.getDelivery().stream().collect(Collectors.toMap(Delivery::getId, Delivery::getName));
    }
}
