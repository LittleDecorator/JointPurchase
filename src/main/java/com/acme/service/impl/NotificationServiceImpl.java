package com.acme.service.impl;

import com.acme.model.BaseModel;
import com.acme.model.Notification;
import com.acme.model.Order;
import com.acme.model.Subject;
import com.acme.repository.NotificationRepository;
import com.acme.service.NotificationService;
import com.acme.service.SmsService;
import com.acme.service.SubjectService;
import com.acme.util.GmailHelper;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.Strings;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import javax.persistence.Entity;
import java.io.IOException;
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
        String smsText;
        switch (order.getStatus()){
            case NEW:
                smsText = "Создан новый заказ!"; break;
            case CANCELED:
                smsText = "Заказ отменен клиентом!"; break;
            default:
                smsText = "";
        }
        sendNotification(createNotification(smsText, order), receivers);
    }

    public <T extends BaseModel> void sendErrorNotification(T resource) {
        sendNotification(createNotification("Произошла ошибка! Проверь лог!", resource));
    }

    @Override
    public <T extends BaseModel> Notification createNotification(String text, T resource) {
        Notification result = null;
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{
            Notification notification = new Notification();
            notification.setText(text);
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
    public Notification createNotification(String text) {
        return createNotification(text, null);
    }

    @Override
    public void viewNotification(String id) {
        //TODO: добавить логику отметки что уведомление просмотренно
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
