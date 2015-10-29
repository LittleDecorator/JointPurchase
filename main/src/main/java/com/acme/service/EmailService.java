package com.acme.service;

import com.acme.util.EmailBuilder;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public interface EmailService {

    Session createSession(String user,String pass);
    void send(MimeMessage message) throws MessagingException;
//    public EmailBuilder getBuilder();
    EmailBuilder getBuilder(Session session);
    String getRobotCredential();

    void sendOrderDone(String mailTo);
    boolean sendRegistrationToken(String mailTo,String content);

}
