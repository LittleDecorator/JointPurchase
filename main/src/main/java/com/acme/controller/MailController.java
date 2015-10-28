package com.acme.controller;

import com.acme.service.impl.EmailServiceImpl;
import com.acme.util.Constants;
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

    @RequestMapping("/mail/registration/confirm")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendRegistrationNotification(String recipient){
        notifySend(recipient, Constants.REGISTRATION_CONFIRM, "Registration confirmation");
    }

    @RequestMapping("/mail/registration/done")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendRegistrationDone(String recipient){
        notifySend(recipient, Constants.REGISTRATION_DONE, "Registration confirmation");
    }

    @RequestMapping("/mail/order/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendOrderCreate(String recipient){
        notifySend(recipient,Constants.ORDER_CREATE,"Order confirmation");
    }

    private void notifySend(String recipient,String mailContent,String subject) {
        EmailBuilder builder = emailService.getBuilder(emailService.createSession("bobby", "12345678"));
        try{
            MimeMessage message = builder.setTo(recipient).setFrom(emailService.getRobotCredential()).setHtmlContent(mailContent).setSubject(subject).build();
            emailService.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}
