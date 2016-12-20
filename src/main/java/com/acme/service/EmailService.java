package com.acme.service;

import com.acme.exception.TemplateException;
import com.acme.model.Email;
import com.acme.model.PurchaseOrder;
import com.acme.util.EmailBuilder;
import com.acme.util._EmailBuilder;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface EmailService {

    void sendOrderAccepted(PurchaseOrder order) throws IOException, MessagingException, TemplateException;
    boolean sendRegistrationToken(String mailTo, String content);

//    List<Email> getInboxEmail() throws MessagingException;
//    List<Email> getSendEmail() throws MessagingException;

}
