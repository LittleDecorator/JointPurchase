package com.acme.service.impl;

import com.acme.enums.NotificationType;
import com.acme.model.*;
import com.acme.repository.NotificationRepository;
import com.acme.service.NotificationService;
import com.acme.service.SmsService;
import com.acme.service.SubjectService;
import com.acme.util.GmailHelper;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by nikolay on 07.05.17.
 */

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    SmsService smsService;

    @Autowired
    private GmailHelper helper;

    @Autowired
    SubjectService subjectService;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public void sendOrderNotification(Order order) {
        String receivers = Joiner.on(",").skipNulls().join(subjectService.getAdmins().stream().map(Subject::getPhoneNumber).collect(Collectors.toList()));
        String smsTitle, smsText;
        switch (order.getStatus()){
            case NEW:
                smsTitle = "Создан новый заказ";
                smsText = "Клиент создал новый заказ. Номер - " + order.getUid();
                break;
            case CANCELED:
                smsTitle = "Заказ отменен";
                smsText = "Клиент отменил заказ " + order.getUid();
                break;
            default:
                smsTitle = "";
                smsText = "";
        }
        sendNotification(createNotification(smsTitle, smsText, order), receivers);
    }

    public <T extends BaseModel> void sendErrorNotification(T resource) {
        sendNotification(createNotification("Ошибка!","Проверь лог.", NotificationType.ERROR, resource));
    }

    public <T extends BaseModel> Notification createNotification(String title, String text, NotificationType type, T resource) {
        Notification result = null;
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            Notification notification = new Notification();
            notification.setTitle(title);
            notification.setText(text);
            notification.setType(type);
            if(resource!=null){
                notification.setTargetId(resource.getId());
                notification.setTargetResource(resource.getClass().getSimpleName());
            }
            result = notificationRepository.save(notification);
            transactionManager.commit(status);
        } catch (Exception ex){
            log.error("Ошибка создания уведомления", ex);
            transactionManager.rollback(status);
        }
        return result;
    }

    @Override
    public <T extends BaseModel> Notification createNotification(String title, String text, T resource) {
        return createNotification(title, text, NotificationType.NORMAL, resource);
    }

    @Override
    public Notification createNotification(String title, String text) {
        return createNotification(title, text, null);
    }

    @Override
    public void viewNotification(String id, Credential credential) {
        Notification notification = notificationRepository.findOne(id);
        if(!notification.isViewed()){
            if(notification.isRootOnly() && !credential.getRoleId().contentEquals("root")){
                return;
            }
            notification.setViewedSubjectId(credential.getSubjectId());
            notification.setViewDate(new Date());
            notificationRepository.save(notification);
        }
    }

    @Override
    public void sendNotification(Notification notification){
        try{
            Subject root = subjectService.getRoot();
            if(Strings.isEmpty(root.getPhoneNumber())){
                helper.sendMessage(root.getEmail(), "Уведомление", notification.toString());
            } else {
                smsService.sendSimple(root.getPhoneNumber(), notification.getText());
            }
        } catch (Exception ex){
            log.error("Ошибка отправки уведомления", ex);
        }
    }

    @Override
    public void sendNotification(Notification notification, String receivers) {
        smsService.sendSimple(receivers, notification.getText());
    }
}
