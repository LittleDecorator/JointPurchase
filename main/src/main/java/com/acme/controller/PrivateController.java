package com.acme.controller;

import com.acme.gen.domain.PurchaseOrder;
import com.acme.service.AuthService;
import io.jsonwebtoken.Claims;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/private")
public class PrivateController {

    /*@RequestMapping(value = "/role/{role}", method = RequestMethod.GET)
    public boolean login(@PathVariable final String role, HttpServletRequest request) throws ServletException {
        System.out.println("request -> "+request);
        System.out.println("role -> "+role);
        final Claims claims = (Claims) request.getAttribute("claims");
        System.out.println(claims);
        boolean res = ((String) claims.get("roles")).contentEquals(role);
        System.out.println(res?"TRUE":"False");
        return res;
    }*/

    @Autowired
    private AuthService authService;

    @Autowired
    private MailController mailController;

    @Autowired
    private OrderController orderController;


    @RequestMapping(method = RequestMethod.POST,value = "/order/create")
    public void privateOrderProcess(@RequestBody String input,HttpServletRequest request) throws ParseException, IOException {
        final Claims claims = (Claims) request.getAttribute("claims");
        System.out.println("Claims -> " + claims);
        PurchaseOrder purchaseOrder = orderController.createOrUpdateOrder(input);
        mailController.sendOrderCreate(purchaseOrder.getRecipientEmail());
    }

}
