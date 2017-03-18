package com.acme.controller;

import com.acme.annotation.JsonAttribute;
import com.acme.exception.TemplateException;
import com.acme.helper.LoginResponse;
import com.acme.helper.RegistrationData;
import com.acme.helper.SubjectCredential;
import com.acme.model.Credential;
import com.acme.repository.CredentialRepository;
import com.acme.service.AuthService;
import com.acme.service.TokenService;
import com.acme.util.PasswordHashing;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    CredentialRepository credentialRepository;

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
        return null;
    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public boolean registrationConfirm(@RequestBody RegistrationData input) throws ServletException, ParseException, UnsupportedEncodingException {
        return authService.register(input);
    }

    @RequestMapping(value = "/restore",method = RequestMethod.POST)
//    public String restorePasswordConfirm(@RequestBody String login) throws ServletException, ParseException, IOException, TemplateException, MessagingException {
    public void restorePasswordConfirm(@RequestBody String login) throws ServletException, ParseException, IOException, TemplateException, MessagingException {
        System.out.println(login);
        JSONParser parser = new JSONParser();
        String elogin = ((JSONObject)parser.parse(login)).get("data").toString();
        System.out.println(elogin);
//        return authService.restore(elogin);
        authService.restore(elogin);
    }

    @RequestMapping(value = "/change",method = RequestMethod.POST)
    public Boolean changePassword(@JsonAttribute("oldPassword") String oldPassword, @JsonAttribute("newPassword")String newPassword) throws ServletException, ParseException, UnsupportedEncodingException {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
        Credential credential = credentialRepository.findOne(authService.getClaims(servletRequest).getId());
        System.out.println(credential);
        if(credential != null && PasswordHashing.validatePassword(oldPassword, credential.getPassword())){
            credential.setPassword(PasswordHashing.hashPassword(authService.decryptPassword(newPassword)));
            credentialRepository.save(credential);
        }
        return Boolean.TRUE;
    }

}
