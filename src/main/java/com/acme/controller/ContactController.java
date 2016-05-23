package com.acme.controller;

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
    SmsService smsService;

    @RequestMapping(method = RequestMethod.POST,value = "/callback/sms")
    public void sendForCallback(@RequestBody String input) throws ParseException, IOException {
        System.out.println(input);
//        smsService.sendCallback();
    }

}