package com.acme.controller;

import com.acme.model.dto.CallbackRequest;
import com.acme.service.NotificationService;
import com.acme.service.SmsService;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/contact")
public class ContactController {

    @Autowired
    NotificationService notificationService;

    @RequestMapping(method = RequestMethod.POST, value = "/callback/sms")
    public void sendForCallback(@RequestBody CallbackRequest request) throws ParseException, IOException {
        notificationService.createNotification("Клиент "+ request.getPhone() + " отправил сообщение", request.getMessage());
    }

}
