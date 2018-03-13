package com.acme.controller;

import com.acme.exception.TemplateException;
import com.acme.model.Order;
import com.acme.model.dto.OrderDto;
import com.acme.model.dto.OrderItemsList;
import com.acme.model.dto.OrderRequest;
import com.acme.model.filter.OrderFilter;
import com.acme.model.dto.mapper.OrderMapper;
import com.acme.service.*;
import java.util.Set;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Контроллер заказов
 */
@RestController
@RequestMapping(value = "/api/order")
@Transactional
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private AuthService authService;
    private OrderService orderService;
    private OrderMapper orderMapper;

    @Autowired
    public OrderController(AuthService authService, OrderService orderService, OrderMapper orderMapper) {
        this.authService = authService;
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    /**
     * Получение всех заказов по фильтру
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Set<OrderDto> getOrders(OrderFilter filter) {
        List<Order> orders = orderService.getAllOrders(filter);
        return orderMapper.toSimpleDto(orders);
    }

    /**
     * Получение заказа по ID
     *
     * @param id - order ID
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public OrderDto getOrder(@PathVariable("id") String id) {
        Order order = orderService.getOrder(id);
        return orderMapper.toDto(order);
    }

    /**
     * Получение заказов по ID клиента
     *
     * @param id - customer ID
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/customer/{id}")
    public Set<OrderDto> getCustomerOrders(@PathVariable("id") String id) {
        List<Order> orders = orderService.getCustomerOrders(id);
        return orderMapper.toSimpleDto(orders);
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
    public OrderDto privateOrderProcess(@RequestBody OrderRequest request) throws ParseException, IOException, MessagingException, TemplateException {
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
    public Set<OrderDto> getOrderHistory(OrderFilter filter) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
        filter.setSubjectId(authService.getClaims(servletRequest).getId());
        List<Order> history = orderService.getHistory(filter);
        return orderMapper.toSimpleDto(history);
    }

    /**
     * Создание заказа
     *
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public OrderDto createOrder(@RequestBody OrderRequest request) {
        Order order = orderService.createOrder(request);
        return orderMapper.toDto(order);
    }

    /**
     * Отмена заказа по ID
     *
     * @param id - order ID
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}/cancel")
    public OrderDto cancelOrder(@PathVariable("id") String id) {
        Order order = orderService.cancelOrder(id);
        return orderMapper.toDto(order);
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
