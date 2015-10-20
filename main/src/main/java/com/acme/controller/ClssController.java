package com.acme.controller;

import com.acme.enums.DeliveryType;
import com.acme.enums.Status;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clss")
public class ClssController {

    @RequestMapping(method = RequestMethod.GET,value = "/order/status")
    public JSONArray getOrderStatus(){
        JSONArray array = new JSONArray();
        JSONObject object;
        for(Status status : Status.values()){
            object = new JSONObject();
            object.put("id",status.ordinal());
            object.put("value",status.getText());
            array.add(object);
        }
        return array;
    }

    @RequestMapping(method = RequestMethod.GET,value = "/order/delivery")
    public JSONArray getOrderDelivery(){
        JSONArray array = new JSONArray();
        JSONObject object;
        for(DeliveryType delivery : DeliveryType.values()){
            object = new JSONObject();
            object.put("id",delivery.ordinal());
            object.put("value",delivery.getValue());
            object.put("info",delivery.getDesc());
            array.add(object);
        }
        return array;
    }
}
