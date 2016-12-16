package com.acme.email.impl;

import com.acme.email.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.mail.internet.InternetAddress;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import static java.nio.charset.Charset.forName;

/**
 * Created by kobzev on 16.12.16.
 */

@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EmailImpl implements Email, Serializable {

	private static final long serialVersionUID = 634175529482595823L;

	@NonNull
	private InternetAddress from;

	private InternetAddress replyTo;

	private Collection<InternetAddress> to;

	private Collection<InternetAddress> cc;

	private Collection<InternetAddress> bcc;

	private
	@NonNull
	String subject;

	private
	@NonNull
	String body;

	private Collection<EmailAttachmentImpl> attachments;

	private Charset encoding = forName("UTF-8");

	private Locale locale;

	private Date sentAt;

}
