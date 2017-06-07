package com.acme;

import com.acme.email.oauth.OAuth2Authenticator;
import com.sun.mail.smtp.SMTPTransport;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import static com.acme.email.oauth.OAuth2Authenticator.initialize;

/**
 * Created by kobzev on 07.06.17.
 */
public class AuthenticatorTest {

	/**
	 * Authenticates to IMAP with parameters passed in on the commandline.
	 */
	public static void main(String args[]) throws Exception {
//		if (args.length != 2) {
//			System.err.println(
//					"Usage: OAuth2Authenticator <email> <oauthToken>");
//			return;
//		}
		String email = "robot.grimmstory@gmail.com";
		String oauthToken = "ya29.GlxiBD74VzMgXRvElbhBn39Hsu1md8OLjfMzW3RVD4E-MvN6cc3baAixxtHqhRSJSvcUIWV2O2KwIn8lFm2IjfBAkaASByyYkZSdE3oYNNF5rL42miXXvmDb5l9XEA";

		initialize();

//		IMAPStore imapStore = connectToImap("imap.gmail.com", 993, email, oauthToken, true);
//		System.out.println("Successfully authenticated to IMAP.\n");
//		SMTPTransport smtpTransport = connectToSmtp("smtp.gmail.com", 587, email, oauthToken, true);
		Session session = OAuth2Authenticator.getSmtpSession(oauthToken, true);
		SMTPTransport transport = OAuth2Authenticator.connectToSmtp("smtp.gmail.com", 587, email, session);
		System.out.println("Successfully authenticated to SMTP.");
		DataHandler handler = new DataHandler(new ByteArrayDataSource("TEST CONTENT".getBytes(), "text/plain"));
		MimeMessage msg = new MimeMessage(session);
		msg.setSender(new InternetAddress(email));
		msg.setDataHandler(handler);
		msg.setSubject("some subject");
		msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse("kobzeff.inc@mail.ru"));
		transport.sendMessage(msg, msg.getAllRecipients());
	}

}
