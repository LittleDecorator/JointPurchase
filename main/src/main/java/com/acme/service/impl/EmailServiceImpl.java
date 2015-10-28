package com.acme.service.impl;

import com.acme.service.EmailService;
import com.acme.util.Constants;
import com.acme.util.EmailBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService{

	@Autowired
	Session robotMailSession;

    @Autowired
    Properties mailProperties;

    /**
     * Create mail builder session for ROBOT as DEFAULT session
     *
     * @return EmailBuilder
     */
	public EmailBuilder getBuilder(){
		return getBuilder(robotMailSession);
	}

    /**
     * Send given message
     *
     * @param message
     * @throws MessagingException
     */
	public void send(MimeMessage message) throws MessagingException {
		Transport.send(message);
	}

    /**
     * Set email session explicitly
     *
     * @param session
     */
	public void setMailSession(Session session){
		this.robotMailSession = session;
	}

    /**
     * Create and return new session for defined user
     *
     * @param user - must contained login and host. Example: user@host.com
     * @param pass - user password for mail server auth
     * @return
     */
    @Override
    public Session createSession(String user, String pass) {
        Session session = Session.getInstance(mailProperties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pass);
                    }
                });
        return session;
    }

    /**
     * Return EmailBuilder for given Session
     *
     * @param session
     * @return
     */
    @Override
    public EmailBuilder getBuilder(Session session) {
        return EmailBuilder.getBuilder(session);
    }

    @Override
    public String getRobotCredential() {
        return Constants.ROBOT_NAME+"@"+mailProperties.getProperty("mail.smtp.host");
    }
}
