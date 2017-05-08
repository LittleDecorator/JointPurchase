package com.acme.service;

import com.acme.model.BaseModel;
import com.acme.model.Notification;
import com.acme.model.Order;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Created by nikolay on 07.05.17.
 */
public interface NotificationService {

    void sendOrderNotification(Order order);

    Notification createNotification(String text);

    <T extends BaseModel> void sendErrorNotification(T resource);

    <T extends BaseModel> Notification createNotification(String text, T resource);

    void viewNotification(String id);

    void sendNotification(Notification notification);

    void sendNotification(Notification notification, String receivers);

}
