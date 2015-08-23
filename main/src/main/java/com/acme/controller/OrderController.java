package com.acme.controller;

import com.acme.gen.domain.OrderItems;
import com.acme.gen.domain.OrderItemsExample;
import com.acme.gen.domain.PurchaseOrder;
import com.acme.gen.domain.PurchaseOrderExample;
import com.acme.gen.mapper.OrderItemsMapper;
import com.acme.gen.mapper.PurchaseOrderMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/order")
public class OrderController{

    @Autowired
    private PurchaseOrderMapper orderMapper;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @RequestMapping(method = RequestMethod.GET)
    public List<PurchaseOrder> getOrders() {
        return orderMapper.selectByExample(new PurchaseOrderExample());
    }

    @RequestMapping(method = RequestMethod.GET,value = "/customer/{id}")
    public List<PurchaseOrder> getCustomerOrders(@PathVariable("id") String id) {
        PurchaseOrderExample example = new PurchaseOrderExample();
        example.createCriteria().andSubjectIdEqualTo(id);
        return orderMapper.selectByExample(example);
    }

    @RequestMapping(method = RequestMethod.POST)
    public PurchaseOrder createOrder(@RequestBody PurchaseOrder input) throws ParseException, IOException {

        System.out.println("createOrder");
        System.err.println(input);

        /*ObjectMapper mapper = new ObjectMapper();
        JSONParser parser=new JSONParser();
        JSONObject main = (JSONObject) parser.parse(input);
        String orderS = ((JSONObject) main.get("order")).toJSONString();

        PurchaseOrder order = mapper.readValue(orderS, PurchaseOrder.class);

        if(order.getId()!=null){
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            orderMapper.insertSelective(order);
        }

        JSONArray itemsArray = (JSONArray) main.get("items");
        for(int i = 0;i<itemsArray.size();i++){
            String item = ((JSONObject) itemsArray.get(i)).toJSONString();
            OrderItems orderItem = mapper.readValue(item, OrderItems.class);
            orderItem.setOrderId(order.getId());
            if(orderItem.getId()!=null){
                orderItemsMapper.updateByPrimaryKeySelective(orderItem);
            } else {
                orderItemsMapper.insertSelective(orderItem);
            }
        }
        return order;*/
        return null;
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public void deleteOrder(@PathVariable("id") String id){
        //delete order bind items
        OrderItemsExample orderItemsExample = new OrderItemsExample();
        orderItemsExample.createCriteria().andOrderIdEqualTo(id);
        orderItemsMapper.deleteByExample(orderItemsExample);
        //delete order itself
        orderMapper.deleteByPrimaryKey(id);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public PurchaseOrder getOrder(@PathVariable("id") String id){
        return orderMapper.selectByPrimaryKey(id);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{id}/items")
    public List<OrderItems> getOrderItems(@PathVariable("id") String id) {
        OrderItemsExample example = new OrderItemsExample();
        example.createCriteria().andOrderIdEqualTo(id);
        return orderItemsMapper.selectByExample(example);
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}/item/{itemId}")
    public void removeOrderItem(@PathVariable("id") String orderId,@PathVariable("itemId") String itemId) {
        OrderItemsExample example = new OrderItemsExample();
        example.createCriteria().andItemIdEqualTo(itemId).andOrderIdEqualTo(orderId);
        orderItemsMapper.deleteByExample(example);
    }

}
