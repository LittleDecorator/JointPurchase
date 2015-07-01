package com.acme.controller;

import io.jsonwebtoken.Claims;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class ApiController {

    @RequestMapping(value = "role/{role}", method = RequestMethod.GET)
    public boolean login(@PathVariable final String role, HttpServletRequest request) throws ServletException {
        System.out.println("request -> "+request);
        System.out.println("role -> "+role);
        final Claims claims = (Claims) request.getAttribute("claims");
        System.out.println(claims);
        boolean res = ((String) claims.get("roles")).contentEquals(role);
        System.out.println(res?"TRUE":"False");
        return res;
    }

}
