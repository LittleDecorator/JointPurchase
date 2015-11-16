package com.acme.service;

import com.acme.model.domain.Email;
import com.acme.util.EmailBuilder;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.List;

public interface EmailService {

    Session createSession(String user,String pass);
    void send(MimeMessage message) throws MessagingException;
//    public EmailBuilder getBuilder();
    EmailBuilder getBuilder(Session session);
    String getRobotCredential();

    void sendOrderDone(String mailTo);
    boolean sendRegistrationToken(String mailTo,String content);

    List<Email> getInboxEmail() throws MessagingException;
    List<Email> getSendEmail() throws MessagingException;

}
