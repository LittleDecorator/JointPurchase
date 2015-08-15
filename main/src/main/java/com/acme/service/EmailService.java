/*
package com.acme.service;

import com.acme.util.EmailBuilder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

public interface EmailService {

	*/
/*void sendMail(String smtpHost, String smtpPort, final String emailLogin, final String emailPassword,
                  String fromEmail, String toEmail, String subject, String messageBody, File... attachements) throws MessagingException;*//*


    EmailBuilder getBuiler();
    void send(MimeMessage message) throws MessagingException;
}
*/
