package com.acme.controller;

import com.acme.exception.TemplateException;
import com.acme.model.Item;
import com.acme.model.OrderItem;
import com.acme.model.PurchaseOrder;
import com.acme.model.dto.OrderRequest;
import com.acme.model.dto.OrderView;
import com.acme.model.filter.OrderFilter;
import com.acme.repository.ItemRepository;
import com.acme.repository.OrderItemRepository;
import com.acme.repository.PurchaseOrderRepository;
import com.acme.service.AuthService;
import com.acme.service.EmailService;
import com.google.common.collect.Lists;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
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
    public List<OrderView> getOrders(OrderFilter filter) {
        return purchaseOrderRepository.getAll(filter);
    }

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public List<OrderView> getOrderHistory(OrderFilter filter) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
        filter.setSubjectId(authService.getClaims(servletRequest).getId());
        System.out.println(filter);
        return purchaseOrderRepository.getAll(filter);
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
    public PurchaseOrder privateOrderProcess(@RequestBody OrderRequest request) throws ParseException, IOException, MessagingException, TemplateException {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
        request.getOrder().setSubjectId(authService.getClaims(servletRequest).getId());
        PurchaseOrder purchaseOrder = createOrUpdateOrder(request);
        emailService.sendOrderAccepted(purchaseOrder);
        return purchaseOrder;
    }

    /**
     * Update Order if ID present, else Create new Order
     * @param request
     * @return
     * @throws ParseException
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.POST)
    public PurchaseOrder createOrUpdateOrder(@RequestBody OrderRequest request) throws ParseException, IOException {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            PurchaseOrder order = request.getOrder();
            if(order.getId()!=null){
                purchaseOrderRepository.updateSelectiveById(order);
            } else {
                order.setUid(System.currentTimeMillis());
                System.out.println("insert " + order);
                purchaseOrderRepository.insertSelective(order);
                System.out.println(order);
            }

            //delete old links
            orderItemRepository.deleteByOrderId(order.getId());
            //insert new links
            for(OrderItem orderItem : request.getItems()){
                orderItem.setOrderId(order.getId());
                orderItemRepository.insertSelective(orderItem);
            }
            transactionManager.commit(status);
            return order;
        } catch (Exception e){
            System.out.println("ERROR HAPPEN");
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
