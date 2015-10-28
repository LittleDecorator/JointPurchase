package com.acme.controller;

import com.acme.gen.domain.*;
import com.acme.gen.mapper.ItemMapper;
import com.acme.gen.mapper.OrderItemMapper;
import com.acme.gen.mapper.PurchaseOrderMapper;
import com.acme.model.domain.Node;
import com.acme.service.AuthService;
import com.acme.service.EmailService;
import com.acme.util.Constants;
import com.acme.util.EmailBuilder;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import io.jsonwebtoken.Claims;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping(value = "/order")
public class OrderController{

    @Autowired
    private PurchaseOrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private MailController mailController;

    @Autowired
    AuthService authService;

    @Autowired
    EmailService emailService;

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

    @RequestMapping(method = RequestMethod.POST,value = "/personal")
    public void privateOrderProcess(@RequestBody String input,ServletRequest servletRequest) throws ParseException, IOException {
        System.out.println(servletRequest != null ? "NOT NULL" : "NULL");
        System.out.println("Claims -> " + authService.getClaims(servletRequest));
        PurchaseOrder purchaseOrder = createOrUpdateOrder(input);


//        mailController.sendOrderCreate(purchaseOrder.getRecipientEmail());

        EmailBuilder builder = emailService.getBuilder(emailService.createSession("bobby", "12345678"));
        try{
            MimeMessage message = builder.setTo(purchaseOrder.getRecipientEmail()).setFrom(emailService.getRobotCredential()).setHtmlContent(Constants.ORDER_CREATE).setSubject("Order test").build();
            emailService.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update Order if ID present, else Create new Order
     * @param input
     * @return
     * @throws ParseException
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.POST)
    public PurchaseOrder createOrUpdateOrder(@RequestBody String input) throws ParseException, IOException {

        System.out.println(input);

        ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        JSONParser parser=new JSONParser();
        JSONObject main = (JSONObject) parser.parse(String.valueOf(input));
        String orderS = ((JSONObject) main.get("order")).toJSONString();

        PurchaseOrder order = mapper.readValue(orderS, PurchaseOrder.class);

        if(order.getId()!=null){
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            order.setUid(System.currentTimeMillis());
            orderMapper.insertSelective(order);
        }

        JSONArray itemsArray = (JSONArray) main.get("items");
        List<OrderItem> orderItems = mapper.readValue(itemsArray.toJSONString(), new TypeReference<List<OrderItem>>(){});
        //delete old links
        OrderItemExample deleteExample = new OrderItemExample();
        deleteExample.createCriteria().andOrderIdEqualTo(order.getId());
        orderItemMapper.deleteByExample(deleteExample);
        //insert new links
        for(OrderItem orderItem : orderItems){
            orderItem.setOrderId(order.getId());
            orderItemMapper.insertSelective(orderItem);
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
    public JSONArray getOrderItems(@PathVariable("id") String id) {
        System.out.println("getOrderItems");
        //get link info
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andOrderIdEqualTo(id);
        List<OrderItem> orderItems =  orderItemMapper.selectByExample(example);
        List<String> itemIdList = Lists.transform(orderItems, new Function<OrderItem, String>() {
            @Nullable
            @Override
            public String apply(OrderItem orderItem) {
                return orderItem.getItemId();
            }
        });
        System.out.println(itemIdList);
        //get items
        JSONArray array = new JSONArray();
        if(itemIdList.size()>0){
            ItemExample itemExample = new ItemExample();
            itemExample.createCriteria().andIdIn(itemIdList);
            List<Item> items = itemMapper.selectByExample(itemExample);

            //create result

            JSONObject object;
            for(OrderItem orderItem : orderItems){
                for(Item item : items){
                    if(orderItem.getItemId().contentEquals(item.getId())){
                        object = new JSONObject();
                        object.put("item",item);
                        object.put("cou",orderItem.getCou());
                        array.add(object);
                    }
                }
            }
        }

        return array;
    }

}
