package com.acme.controller;

import com.acme.model.Notification;
import com.acme.repository.NotificationRepository;
import com.acme.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by nikolay on 08.05.17.
 */

@RestController
@RequestMapping(value = "/notification")
public class NotificationController {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    NotificationService notificationService;

    /**
     * Получение всех уведомлений
     **/
    @RequestMapping(method = RequestMethod.GET)
    public List<Notification> getNotifications() {
        return notificationRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{id}")
    public void setViewed(@PathVariable("id") String id) {
        notificationRepository.findOne(id);
        notificationService.viewNotification(id);
    }
}
