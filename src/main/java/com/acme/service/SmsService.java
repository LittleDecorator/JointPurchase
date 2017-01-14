package com.acme.service;

import com.acme.sms.SMSAccount;

public interface SmsService {

    void sendCallback(SMSAccount account, String from, String to, String text);

    void passChangeConfirm();

}
