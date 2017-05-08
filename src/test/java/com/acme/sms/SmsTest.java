package com.acme.sms;

import com.acme.service.SmsService;
import com.acme.service.impl.SmsServiceImpl;

/**
 * Created by nikolay on 07.05.17.
 */
public class SmsTest {

    public static void main(String args[]){
        SmsService service = new SmsServiceImpl();
//        service.sendSimple("79258552096","Создан новый заказ!", false);
    }

}
