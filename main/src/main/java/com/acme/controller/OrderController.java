package com.acme.controller;

import com.acme.gen.domain.OrderItem;
import com.acme.gen.domain.OrderItemExample;
import com.acme.gen.domain.PurchaseOrder;
import com.acme.gen.domain.PurchaseOrderExample;
import com.acme.gen.mapper.OrderItemMapper;
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
    private OrderItemMapper orderItemMapper;


    /**
     * Get all orders
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<PurchaseOrder> getOrders() {
        return orderMapper.selectByExample(new PurchaseOrderExample());
    }

    /**
     * Get specific customer order
     * @param id - customer ID
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/customer/{id}")
    public List<PurchaseOrder> getCustomerOrders(@PathVariable("id") String id) {
        PurchaseOrderExample example = new PurchaseOrderExample();
        example.createCriteria().andSubjectIdEqualTo(id);
        return orderMapper.selectByExample(example);
    }

    /**
     * Update Order if ID present, else Create new Order
     * @param input
     * @return
     * @throws ParseException
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.POST)
    public PurchaseOrder createOrUpdateOrder(@RequestBody PurchaseOrder input) throws ParseException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        JSONParser parser=new JSONParser();
        JSONObject main = (JSONObject) parser.parse(String.valueOf(input));
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
            OrderItem orderItem = mapper.readValue(item, OrderItem.class);
            orderItem.setOrderId(order.getId());
            if(orderItem.getId()!=null){
                orderItemMapper.updateByPrimaryKeySelective(orderItem);
            } else {
                orderItemMapper.insertSelective(orderItem);
            }
        }
        return order;
    }

    /**
     * Delete specific order
     * @param id - order ID
     */
    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public void deleteOrder(@PathVariable("id") String id){
        //delete order bind items
        OrderItemExample orderItemsExample = new OrderItemExample();
        orderItemsExample.createCriteria().andOrderIdEqualTo(id);
        orderItemMapper.deleteByExample(orderItemsExample);
        //delete order itself
        orderMapper.deleteByPrimaryKey(id);
    }

    /**
     * Get Order detail
     * @param id - order ID
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public PurchaseOrder getOrder(@PathVariable("id") String id){
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * Get Items list in order
     * @param id - order ID
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/{id}/items")
    public List<OrderItem> getOrderItems(@PathVariable("id") String id) {
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andOrderIdEqualTo(id);
        return orderItemMapper.selectByExample(example);
    }

}
