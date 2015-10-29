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
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService{

    @Autowired
    Properties mailProperties;

    /**
     * Create mail builder session for ROBOT as DEFAULT session
     *
     * @return EmailBuilder
     */
//	public EmailBuilder getBuilder(){
//        System.out.println(robotMailSession!=null?"NOT NULL":"IS NULL");
//		return getBuilder(robotMailSession);
//	}

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
     * Create and return new session for defined user
     *
     * @param user - must contained login and host. Example: user@host.com
     * @param pass - user password for mail server auth
     * @return
     */
    @Override
    public Session createSession(String user, String pass) {
        System.out.println(mailProperties.stringPropertyNames());
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
        String res = Constants.ROBOT_NAME+"@"+mailProperties.getProperty("mail.smtp.host");
        System.out.println(res);
        return res;
    }

    public void sendOrderDone(String mailTo){
        EmailBuilder builder = getBuilder(createSession("bobby", "12345678"));
        try{
            MimeMessage message = builder.setTo(mailTo).setFrom(getRobotCredential()).setHtmlContent(Constants.ORDER_CREATE).setSubject("Your purchase accepted").build();
            send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean sendRegistrationToken(String mailTo,String content) {
        EmailBuilder builder = getBuilder(createSession("bobby", "12345678"));
        try{
            MimeMessage message = builder.setTo(mailTo).setFrom(getRobotCredential()).setHtmlContent(content).setSubject("Registration confirmation").build();
            send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
