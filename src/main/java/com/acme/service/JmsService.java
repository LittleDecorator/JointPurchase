package com.acme.service;

import com.acme.model.Order;
import com.acme.model.Subject;

/**
 * Created by nikolay on 13.08.17.
 */
public interface JmsService {

    /**
     * Отложенная отправка токена регистрации
     * @param recipient
     * @param token
     */
    void registrationRequest(Subject subject, String recipient, String token);

    /**
     * Отложеная отправка уведомления об успешности регистрации
     * @param subject
     */
    void registrationConfirm(Subject subject);

    /**
     * Отлеженая отправка токена на смену пароля
     * @param subject
     * @param token
     */
    void passwordChangeRequest(Subject subject, String token);

    void orderConfirm(Order order);

}
