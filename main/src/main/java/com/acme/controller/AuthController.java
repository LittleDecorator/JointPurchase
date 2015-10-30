package com.acme.controller;

import com.acme.gen.domain.Credential;
import com.acme.gen.mapper.CredentialMapper;
import com.acme.helper.LoginResponse;
import com.acme.helper.RegistrationData;
import com.acme.helper.SubjectCredential;
import com.acme.service.AuthService;
import com.acme.service.TokenService;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    CredentialMapper credentialMapper;

    @Autowired
    TokenService tokenService;

    @Autowired
    AuthService authService;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public LoginResponse authentication(@RequestBody SubjectCredential subjectCredential) throws ServletException {
        System.out.println("AuthLogin: login credentials -> " + subjectCredential.toString());
        Credential credential = authService.validate(subjectCredential);
        if (credential!=null){
            return new LoginResponse(tokenService.createToken(credential));
        }
//        throw new ServletException("Invalid login");
        return null;
    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public boolean registration(@RequestBody RegistrationData input) throws ServletException, ParseException, UnsupportedEncodingException {
        System.out.println(input);
        return authService.register(input);
    }

}
