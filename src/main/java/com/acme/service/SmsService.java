package com.acme.service;

import com.acme.sms.SMSAccount;

/**
 * Интерфейс работы с sms.ru
 */
public interface SmsService {

    void passChangeConfirm();

    void getDayLimitInfo();

    void getBalanceInfo();

    void sendSimple(SMSAccount account, String from, String to, String text);

    void sendSimple(String to, String text, boolean isTest);

    void sendSimple(String to, String text);

}
