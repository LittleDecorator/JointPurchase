package com.acme.service;

import com.acme.model.Notification;
import com.acme.model.Order;

/**
 * Created by nikolay on 07.05.17.
 */
public interface NotificationService {

    void sendOrderNotification(Order order);

    void createNotification();

    void sendNotification(Notification notification);

}
