//package com.acme.email;
//
//import com.acme.email.impl.EmailAttachmentImpl;
//import com.acme.exception.EmailConversionException;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Component;
//
//import javax.mail.MessagingException;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//import java.io.IOException;
//import java.nio.charset.Charset;
//import java.util.function.Function;
//
//import static com.google.common.base.Optional.fromNullable;
//import static java.util.Objects.nonNull;
//import static java.util.Optional.ofNullable;
//
///**
// * Created by kobzev on 16.12.16.
// */
//
//@Slf4j
//public class EmailToMimeMessage implements Function<Email, MimeMessage> {
//
//	private JavaMailSender javaMailSender;
//
//	public EmailToMimeMessage() {
//	}
//
//	public EmailToMimeMessage(final @NonNull JavaMailSender javaMailSender) {
//		this.javaMailSender = javaMailSender;
//	}
//
//	@Override
//	public MimeMessage apply(final Email email) {
//		final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//		final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,
//				fromNullable(email.getEncoding()).or(Charset.forName("UTF-8")).displayName());
//
//		try {
//			messageHelper.setFrom(email.getFrom());
//			if (ofNullable(email.getReplyTo()).isPresent()) {
//				messageHelper.setReplyTo(email.getReplyTo());
//			}
//			if (ofNullable(email.getTo()).isPresent()) {
//				for (final InternetAddress address : email.getTo()) {
//					messageHelper.addTo(address);
//				}
//			}
//			if (ofNullable(email.getCc()).isPresent()) {
//				for (final InternetAddress address : email.getCc()) {
//					messageHelper.addCc(address);
//				}
//			}
//			if (ofNullable(email.getBcc()).isPresent()) {
//				for (final InternetAddress address : email.getBcc()) {
//					messageHelper.addBcc(address);
//				}
//			}
//			if (ofNullable(email.getAttachments()).isPresent()) {
//				for (final EmailAttachmentImpl attachment : email.getAttachments()) {
//					try {
//						messageHelper.addAttachment(attachment.getAttachmentName(),
//								attachment.getInputStream(), attachment.getContentType().getType());
//					} catch (IOException e) {
//						log.error("Error while converting Email to MimeMessage");
//						throw new EmailConversionException(e);
//					}
//				}
//			}
//			messageHelper.setSubject(ofNullable(email.getSubject()).orElse(""));
//			messageHelper.setText(ofNullable(email.getBody()).orElse(""));
//
//			if (nonNull(email.getSentAt())) {
//				messageHelper.setSentDate(email.getSentAt());
//			}
//		} catch (MessagingException e) {
//			log.error("Error while converting Email to MimeMessage");
//			throw new EmailConversionException(e);
//		}
//
//
//		return mimeMessage;
//	}
//
//}
