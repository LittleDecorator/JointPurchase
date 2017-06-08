package com.acme.service.impl;

import com.acme.service.SmsService;
import com.acme.sms.SMSAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.dezhik.sms.sender.SenderService;
import ru.dezhik.sms.sender.api.InvocationStatus;
import ru.dezhik.sms.sender.api.smsru.send.SMSRuSendRequest;
import ru.dezhik.sms.sender.api.smsru.send.SMSRuSendResponse;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Autowired
    SenderService senderService;

    @Override
    public void passChangeConfirm() {
        //TODO: добавить вариант подтверждения через sms
    }

    @Override
    public void getDayLimitInfo() {
        //TODO: добавить методы получения инфо о лимите
    }

    @Override
    public void getBalanceInfo() {
        //TODO: добавить методы получения инфо о балансе
    }

    @Override
    public void sendSimple(SMSAccount account, String from, String to, String text) {
        //TODO: нужна поддержка SMSAccount'а клиента
    }

    @Override
    public Boolean sendSimple(String to, String text){
        Boolean result;
        SMSRuSendRequest request = new SMSRuSendRequest();
        request.setText(text);
        request.addReceiver(to);
        SMSRuSendResponse sendResponse = senderService.execute(request);
        result = request.getStatus() == InvocationStatus.SUCCESS;
        if (result) {
            log.info("request was executed successfully now you can handle sendResponse");
            log.info(sendResponse.toString());
        } else {
            log.error("request was executed with ERRORS");
            log.info(sendResponse.toString());
        }
//        senderService.shutdown();
        return result;
    }

}
