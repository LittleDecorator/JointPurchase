package com.acme.jms;

import com.acme.enums.NotificationType;
import com.acme.model.Order;
import com.acme.model.Subject;
import com.acme.service.EmailService;
import com.acme.service.NotificationService;
import com.acme.service.SettingsService;
import com.acme.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsMessageHeaderAccessor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by nikolay on 13.08.17.
 */

@Component
public class Receiver {

    private static final Logger log = LoggerFactory.getLogger(Receiver.class);

    private EmailService emailService;
    private NotificationService notificationService;
    private SmsService smsService;
    private SettingsService settingsService;

    @Autowired
    public Receiver(EmailService emailService, NotificationService notificationService, SmsService smsService, SettingsService settingsService) {
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.smsService = smsService;
        this.settingsService = settingsService;
    }

    /**
     * Отправка toke'а регистрации по email.
     * В случае ощибки отправляем sms уведомление
     * @param params
     */
    @JmsListener(destination = "registration_request", containerFactory = "defaultFactory")
    public void receiveRegistrationRequest(Map<String, Object> params) {
        Subject subject = (Subject)params.get("subject");
        log.info("Получено jms сообщение: Запрос на отправку token'а по email для регистрации нового клиента = {0}", subject);
        boolean emailSendResult = !Boolean.FALSE.equals(emailService.sendRegistrationToken((String)params.get("recipient"), (String)params.get("token")));
        // если отправка не удалась, то уведомим
        if(!emailSendResult){
            log.error("Не удалось отправить email для {0}", params.get("recipient"));
            notificationService.createNotification("Регистрация", "Отправка token'а по email", NotificationType.ERROR, subject);
        }
    }

    @JmsListener(destination = "registration_confirm", containerFactory = "defaultFactory")
    public void receiveRegistrationConfirm(Map<String, Object> params) {
        Subject subject = (Subject)params.get("subject");
        log.info("Получено jms сообщение: Запрос на отправку уведомления по email об успешной регистрации нового клиента = {0}", subject);
        try{
            emailService.sendRegistrationConfirm(subject.getEmail());
        } catch (Exception ex){
            log.error("Не удалось отправить email для {0}", subject.getEmail(), ex);
            notificationService.createNotification("Регистрация", "Отправка успешности регистрации по email", NotificationType.ERROR, subject);
        }
    }

    /**
     * Отправка token'а о смене пароля по email.
     * В случае ощибки отправляем sms уведомление
     * @param params
     */
    @JmsListener(destination = "password_change_request", containerFactory = "defaultFactory")
    public void receivePassChangeRequest(Map<String, Object> params) {
        Subject subject = (Subject)params.get("subject");
        log.info("Получено jms сообщение: Запрос на отправку token'а по email для смены пароля клиента = {0}", subject);
        boolean emailSendResult = !Boolean.FALSE.equals(emailService.sendPassChangeToken(subject.getEmail(), (String)params.get("token")));
        // если отправка не удалась, то уведомим
        if(!emailSendResult){
            log.error("Не удалось отправить email для {0}", subject.getEmail());
            notificationService.createNotification("Смена пароля", "Отправка token'а по email", NotificationType.ERROR, subject);
        }
    }

    @JmsListener(destination = "order_confirm", containerFactory = "defaultFactory")
    public void receiveOrderConfirm(Order order, MessageHeaders messageHeaders) {
        log.info("Получено jms сообщение: Запрос на отправку подтверждения заказа = {0} по email ", order);
        /* отправляем на почту писмо с подтверждением заказа */
        if(emailService.sendOrderStatus(order)){
            // если отправка разрешена
            boolean needNotify = settingsService.getBoolean("ORDER_NOTIFICATION.SMS.SEND");
            /* отправляем уведомление администраторам о новом заказе */
            if(needNotify){
                notificationService.sendOrderNotification(order);
            }
        } else {
            //TODO: отправляем мне уведомление, что ничего не работает
            notificationService.sendErrorNotification(order);
        }
    }

    @JmsListener(destination = "sms_notify", containerFactory = "defaultFactory")
    public void receiveSms(Map<String, String> email) {
        System.out.println("Received <" + email + ">");
    }
}
