package com.acme.util;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.UnsupportedEncodingException;

public class EmailBuilder {

    private static MimeMessage message;
    private Multipart multipart;
    private BodyPart messageBodyPart;

    public EmailBuilder setFrom(String fromEmail) throws MessagingException {
        message.setFrom(new InternetAddress(fromEmail));
        return this;
    }

    public EmailBuilder setTo(String toEmail) throws MessagingException {
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        return this;
    }

    public EmailBuilder setSubject(String subject) throws MessagingException {
        message.setSubject(subject);
        return this;
    }

    public EmailBuilder setHtmlContent(String content) throws MessagingException, UnsupportedEncodingException {
        if(multipart == null){
            multipart = new MimeMultipart();
        }
        messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(content, "text/html; charset=utf-8");
        multipart.addBodyPart(messageBodyPart);
        return this;
    }

    public EmailBuilder setAttachments(File... attachements) throws MessagingException {
        if (attachements != null) {
            if(multipart == null){
                multipart = new MimeMultipart();
            }
            for (File file : attachements) {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(file);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(file.getName());
                multipart.addBodyPart(messageBodyPart);
            }
        }
        return this;
    }

    public EmailBuilder setAttachment(byte[] attachment, String mime, String id) throws MessagingException {
        if (attachment != null) {
            if(multipart == null){
                multipart = new MimeMultipart();
            }
            ByteArrayDataSource dataSource = new ByteArrayDataSource( attachment, mime );
            messageBodyPart.setDataHandler( new DataHandler( dataSource ) );
            messageBodyPart.setHeader("Content-ID", id);
            multipart.addBodyPart(messageBodyPart);
        }
        return this;
    }

    public MimeMessage build() throws MessagingException {
        if(multipart != null){
            message.setContent(multipart);
        }
        return message;
    }

    private EmailBuilder() {}

    public static EmailBuilder getBuilder(Session mailSession){
        message = new MimeMessage(mailSession);
        System.out.println("message created");
        return new EmailBuilder();
    }

}
