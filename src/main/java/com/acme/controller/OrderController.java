package com.acme.controller;

import com.acme.exception.TemplateException;
import com.acme.model.Order;
import com.acme.model.OrderView;
import com.acme.model.dto.OrderItemsList;
import com.acme.model.dto.OrderRequest;
import com.acme.model.filter.OrderFilter;
import com.acme.service.*;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/order")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    AuthService authService;

    @Autowired
    OrderService orderService;

    /**
     * Получение всех заказов по фильтру
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<OrderView> getOrders(OrderFilter filter) {
        log.info("Получение заказов по фильтру: {0}", filter);
        return orderService.getAllOrders(filter);
    }

    /**
     * Получение заказа по ID
     *
     * @param id - order ID
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public Order getOrder(@PathVariable("id") String id) {
        return orderService.getOrder(id);
    }

    /**
     * Получение заказов по ID клиента
     *
     * @param id - customer ID
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/customer/{id}")
    public List<OrderView> getCustomerOrders(@PathVariable("id") String id) {
        return orderService.getCustomerOrders(id);
    }

    /**
     * Создание заказа авторизованного клиента.
     * Клиенту также будет отправлено уведомление (sms|email) о создание заказа
     *
     * @param request
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws MessagingException
     * @throws TemplateException
     */
    @RequestMapping(method = RequestMethod.POST, value = "/personal")
    public Order privateOrderProcess(@RequestBody OrderRequest request) throws ParseException, IOException, MessagingException, TemplateException {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
        request.getOrder().setSubjectId(authService.getClaims(servletRequest).getId());
        return createOrder(request);
    }

    /**
     * Получение истории заказов авторизованного клиента
     *
     * @return
     */
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public List<OrderView> getOrderHistory(OrderFilter filter) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
        filter.setSubjectId(authService.getClaims(servletRequest).getId());
        return orderService.getHistory(filter);
    }

    /**
     * Создание заказа
     *
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public Order createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    /**
     * Отмена заказа по ID
     *
     * @param id - order ID
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}/cancel")
    public Order cancelOrder(@PathVariable("id") String id) {
        return orderService.cancelOrder(id);
    }

    /**
     * Удаление заказа по ID
     *
     * @param id - order ID
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public void deleteOrder(@PathVariable("id") String id) {
        orderService.deleteOrder(id);
    }

    /**
     * Получение товара из заказа
     *
     * @param id - order ID
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/items")
    public List<OrderItemsList> getOrderItems(@PathVariable("id") String id) {
        return orderService.getOrderItems(id);
    }
}
