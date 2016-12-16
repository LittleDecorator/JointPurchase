package com.acme.email;

import com.acme.email.impl.EmailAttachmentImpl;

import javax.mail.internet.InternetAddress;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kobzev on 16.12.16.
 */
public interface Email {

	InternetAddress getFrom();

	InternetAddress getReplyTo();

	Collection<InternetAddress> getTo();

	Collection<InternetAddress> getCc();

	Collection<InternetAddress> getBcc();

	default String getSubject() {
		return "";
	}

	default String getBody() {
		return "";
	}

	Collection<EmailAttachmentImpl> getAttachments();

	Charset getEncoding();

	Locale getLocale();

	Date getSentAt();

	void setSentAt(Date sentAt);

}
