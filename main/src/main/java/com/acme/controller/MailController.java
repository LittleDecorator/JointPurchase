package com.acme.controller;

import com.acme.service.impl.EmailServiceImpl;
import com.acme.util.EmailBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@RestController
public class MailController {

    @Autowired
    private EmailServiceImpl emailService;

    @RequestMapping("/mail")
    @ResponseStatus(HttpStatus.CREATED)
    public void send() throws MessagingException, UnsupportedEncodingException {

        String html = "<a href='http://localhost:7979/public/auth/confirm/test'>Confirm test user registration</a>";

        EmailBuilder builder = emailService.getBuiler();
        MimeMessage message = builder.setTo("kobzeff.inc@mail.ru").setFrom("purchase@auth.com").setHtmlContent(html).setSubject("Registration confirmation").build();
        emailService.send(message);
    }

}
