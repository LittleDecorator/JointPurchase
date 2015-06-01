package com.acme.controller;

import com.acme.gen.domain.Credentials;
import com.acme.gen.domain.CredentialsExample;
import com.acme.gen.mapper.CredentialsMapper;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    CredentialsMapper credentialsMapper;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public LoginResponse auth(@RequestBody UserLogin login) throws ServletException {
        if (!Strings.isNullOrEmpty(login.name)){
            CredentialsExample example = new CredentialsExample();
            example.createCriteria().andLoginEqualTo(login.name);
            List<Credentials> credentiaList = credentialsMapper.selectByExample(example);
            if(credentiaList.size()>0){
                Credentials credential = credentiaList.get(0);
                System.out.println("login -> "+credential.getLogin());
                System.out.println("role -> "+credential.getRoleId());
                return new LoginResponse(Jwts.builder().setSubject(login.name).claim("roles", credential.getRoleId()).setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, "secretkey").compact());
            }
        }
        throw new ServletException("Invalid login");
    }



    private static class UserLogin {
        public String name;
        public String password;
    }

    private static class LoginResponse {
        public String token;

        public LoginResponse(final String token) {
            this.token = token;
        }
    }
}
