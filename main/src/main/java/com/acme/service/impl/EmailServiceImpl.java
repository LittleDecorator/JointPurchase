package com.acme.service.impl;

import com.acme.util.EmailBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl {

	@Autowired
	Session mailSession;

	public EmailBuilder getBuiler(){
		return EmailBuilder.getBuilder(mailSession);
	}

	public void send(MimeMessage message) throws MessagingException {
		Transport.send(message);
	}
}
