package com.acme.service;

import com.acme.util.EmailBuilder;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public interface EmailService {

    public void setMailSession(Session session);
    public Session createSession(String user,String pass);
    public void send(MimeMessage message) throws MessagingException;
    public EmailBuilder getBuilder();
    public EmailBuilder getBuilder(Session session);
    public String getRobotCredential();

}
