package com.acme.service.impl;

import com.acme.constant.Constants;
import com.acme.model.Email;
import com.acme.service.EmailService;
import com.acme.util.EmailBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
@PropertySource("classpath:mail.properties")
public class EmailServiceImpl implements EmailService {

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
        Set<String> names = mailProperties.stringPropertyNames();
        for(String str : names) {
            System.out.println(mailProperties.getProperty(str));
        }

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
        String res = "GrimmStory <"+ Constants.ROBOT_NAME+"@"+mailProperties.getProperty("mail.smtp.host")+">";
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

    @Override
    public List<Email> getInboxEmail() throws MessagingException {
        List<Email> emails = null;
        MimeMessage msg;
        Email email;

        Store store = createSession("bobby", "12345678").getStore("pop3");
        store.connect("80.78.247.250", null, null);
        Folder inbox = store.getFolder("INBOX");

        inbox.open(Folder.READ_ONLY);
        int cou = inbox.getMessageCount();
        if(cou>0){
            emails = new ArrayList<>();
            for(int i=1;i<=cou;i++){
                msg = (MimeMessage) inbox.getMessage(i);
                email = new Email();

                String subFrom = Arrays.toString(msg.getFrom());
                subFrom = subFrom.substring(subFrom.indexOf('<')+1,subFrom.length()-2);
                email.setId(msg.getMessageID());
                email.setFrom(subFrom);
                email.setDate(msg.getSentDate());
                email.setIsNew(msg.isSet(Flags.Flag.RECENT));
                email.setSubject(msg.getSubject());
                emails.add(email);
            }
        }
        System.out.println(emails);
        return emails;
    }

    @Override
    public List<Email> getSendEmail() throws MessagingException {
        List<Email> emails = null;
        MimeMessage msg;
        Email email;

        Store store = createSession("bobby", "12345678").getStore("pop3");
        store.connect("80.78.247.250", null, null);
        Folder inbox = store.getFolder("SEND");

        inbox.open(Folder.READ_ONLY);
        int cou = inbox.getMessageCount();
        if(cou>0){
            emails = new ArrayList<>();
            for(int i=1;i<=cou;i++){
                msg = (MimeMessage) inbox.getMessage(i);
                email = new Email();
                email.setId(msg.getMessageID());
                email.setTo(Arrays.asList(msg.getRecipients(Message.RecipientType.TO)).toString());
                email.setDate(msg.getSentDate());
                email.setSubject(msg.getSubject());
                emails.add(email);
            }
        }
        System.out.println(emails);
        return emails;
    }
}
