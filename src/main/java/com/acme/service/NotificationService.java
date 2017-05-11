package com.acme.service;

import com.acme.enums.NotificationType;
import com.acme.model.*;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Created by nikolay on 07.05.17.
 */
public interface NotificationService {

    void sendOrderNotification(Order order);

    Notification createNotification(String title, String text);

    <T extends BaseModel> void sendErrorNotification(T resource);

    <T extends BaseModel> Notification createNotification(String title, String text, T resource);

    <T extends BaseModel> Notification createNotification(String title, String text, NotificationType type, T resource);

    void viewNotification(String id, Credential credential);

    void sendNotification(Notification notification);

    void sendNotification(Notification notification, String receivers);

}
