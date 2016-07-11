package com.acme.controller;

import com.acme.model.Item;
import com.acme.model.OrderItem;
import com.acme.model.PurchaseOrder;
import com.acme.model.dto.OrderView;
import com.acme.repository.ItemRepository;
import com.acme.repository.OrderItemRepository;
import com.acme.repository.PurchaseOrderRepository;
import com.acme.service.AuthService;
import com.acme.service.EmailService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/order")
public class OrderController{

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    AuthService authService;

    @Autowired
    EmailService emailService;

    @Autowired
    private PlatformTransactionManager transactionManager;


    /**
     * Get all orders
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<OrderView> getOrders() {
        return purchaseOrderRepository.getAll();
    }

    /**
     * Get specific customer order
     * @param id - customer ID
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/customer/{id}")
    public List<OrderView> getCustomerOrders(@PathVariable("id") String id) {
        return purchaseOrderRepository.getBySubjectId(id);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/personal")
    public String privateOrderProcess(@RequestBody String input,ServletRequest servletRequest) throws ParseException, IOException {
        System.out.println(servletRequest != null ? "NOT NULL" : "NULL");
        System.out.println("Claims -> " + authService.getClaims(servletRequest));
        PurchaseOrder purchaseOrder = createOrUpdateOrder(input);
        emailService.sendOrderDone(purchaseOrder.getRecipientEmail());
        return purchaseOrder.getId();
    }

    /**
     * Update Order if ID present, else Create new Order
     * @param input
     * @return
     * @throws ParseException
     * @throws IOException
     */
//    @Transactional(propagation = Propagation.REQUIRED)
    @RequestMapping(method = RequestMethod.POST)
    public PurchaseOrder createOrUpdateOrder(@RequestBody String input) throws ParseException, IOException {

        ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JSONParser parser=new JSONParser();
        JSONObject main = (JSONObject) parser.parse(String.valueOf(input));
        String orderS = ((JSONObject) main.get("order")).toJSONString();

        PurchaseOrder order = mapper.readValue(orderS, PurchaseOrder.class);

        //try use transaction here
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        System.out.println(status.isCompleted());
        try{
            if(order.getId()!=null){
                purchaseOrderRepository.updateSelectiveById(order);
                System.out.println(status.isCompleted());
            } else {
                order.setUid(System.currentTimeMillis());
                purchaseOrderRepository.insertSelective(order);
            }

            JSONArray itemsArray = (JSONArray) main.get("items");
            List<OrderItem> orderItems = mapper.readValue(itemsArray.toJSONString(), new TypeReference<List<OrderItem>>(){});
            //delete old links
            orderItemRepository.deleteByOrderId(order.getId());
            System.out.println(status.isCompleted());
            //insert new links
            for(OrderItem orderItem : orderItems){
                orderItem.setOrderId(order.getId());
                orderItemRepository.insertSelective(orderItem);
                System.out.println(status.isCompleted());
            }
            transactionManager.commit(status);
            return order;
        } catch (Exception e){
            transactionManager.rollback(status);
            return null;
        }
    }

    /**
     * Delete specific order
     * @param id - order ID
     */
    @RequestMapping(method = RequestMethod.DELETE,value = "/{id}")
    public void deleteOrder(@PathVariable("id") String id){
        //delete order bind items
        orderItemRepository.deleteByOrderId(id);
        //delete order itself
        purchaseOrderRepository.deleteById(id);
    }

    /**
     * Get Order detail
     * @param id - order ID
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public PurchaseOrder getOrder(@PathVariable("id") String id){
        return purchaseOrderRepository.getById(id);
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
        List<OrderItem> orderItems = orderItemRepository.getByOrderId(id);
        List<String> itemIdList = Lists.transform(orderItems, OrderItem::getItemId);
        System.out.println(itemIdList);
        //get items
        JSONArray array = new JSONArray();
        if(itemIdList.size()>0){
            List<Item> items = itemRepository.getByIdList(itemIdList);

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
