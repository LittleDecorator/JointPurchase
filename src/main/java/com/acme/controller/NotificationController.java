package com.acme.controller;

import com.acme.model.Credential;
import com.acme.model.Notification;
import com.acme.model.Subject;
import com.acme.model.filter.NotificationFilter;
import com.acme.repository.CredentialRepository;
import com.acme.repository.NotificationRepository;
import com.acme.repository.SubjectRepository;
import com.acme.service.AuthService;
import com.acme.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by nikolay on 08.05.17.
 */

@RestController
@RequestMapping(value = "/notification")
public class NotificationController {

    @Autowired
    CredentialRepository credentialRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    AuthService authService;

    /**
     * Получение всех уведомлений
     **/
    @RequestMapping(method = RequestMethod.GET)
    public List<Notification> getNotifications(NotificationFilter filter) {
        //TODO: Добавить Pageable как в SubjectController
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
        Credential credential = credentialRepository.findOne(authService.getClaims(servletRequest).getId());

        List<Notification> notifications = notificationRepository.findAll();

        Comparator<Notification> comparator = Comparator.comparing(Notification::isViewed);
        comparator = comparator.thenComparing(Comparator.comparing(Notification::getRootOnly).reversed());
        comparator = comparator.thenComparing(Comparator.comparing(Notification::getDateAdd).reversed());

        if(!credential.getRoleId().contentEquals("root")){
            notifications = notifications.stream().filter(n -> !n.isRootOnly()).sorted(comparator).collect(Collectors.toList());
        } else {
            notifications = notifications.stream().sorted(comparator).collect(Collectors.toList());
        }
        return notifications;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public Notification getNotification(@PathVariable("id") String id) {
        Notification notification = notificationRepository.findOne(id);
        notification.setViewedSubject(subjectRepository.findOne(notification.getViewedSubjectId()));
        return notification;
    }

    /**
     * Отметить как прочитанное
     * @param id
     */
    @RequestMapping(method = RequestMethod.POST, value = "/{id}")
    public void setViewed(@PathVariable("id") String id) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
        Credential credential = credentialRepository.findOne(authService.getClaims(servletRequest).getId());
        notificationService.viewNotification(id, credential);
    }

    /**
     * Получение кол-ва новых уведомлений
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/new/count")
    public Integer getNewCount(){
        return notificationRepository.findAllByViewedSubjectIdAndIsRootOnly(null, false).size();
    }
}
