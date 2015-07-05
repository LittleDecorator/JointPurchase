package com.acme.controller;

import com.acme.gen.domain.Credential;
import com.acme.gen.mapper.CredentialMapper;
import com.acme.service.TokenService;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    CredentialMapper credentialMapper;

    @Autowired
    TokenService tokenService;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public LoginResponse auth(@RequestBody UserLogin login) throws ServletException {
        System.out.println("AuthLogin: login credentials -> "+ login.toString());
        if (!Strings.isNullOrEmpty(login.name)){
            Credential credential = credentialMapper.selectByPrimaryKey(login.name);
            if(credential.getPassword().contentEquals(login.password)){
                return new LoginResponse(tokenService.createToken(credential));
            }
        }
//        throw new ServletException("Invalid login");
        return null;
    }



    private static class UserLogin {
        public String name;
        public String password;

        @Override
        public String toString() {
            return "UserLogin{" +
                    "name='" + name + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }

    private static class LoginResponse {
        public String token;

        public LoginResponse(final String token) {
            this.token = token;
        }
    }

}
