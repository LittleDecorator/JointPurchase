package com.acme;

import com.acme.email.oauth.OAuth2Authenticator;
import com.sun.mail.smtp.SMTPTransport;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by nikolay on 06.06.17.
 */
public class TokenMailSend {

    public static void main(String[] args){
        sendEmail("TEST TOKEN");
    }

    private static void sendEmail(String _emailBody){

        String accessToken = "ya29.GlthBEIHqS6h9rRe1JNquPc7ughvtBVS4YA3MmuYf8wM1vHENr0R0kl-5-CbDUPSluT0z9d3jyRlRDVPS-S-cGgRZhG7GFLsA4jx-UlMoSefqbzIUKhLA8bvf6Bi";
        String userName = "robot.grimmstory@gmail.com";

        try{
            OAuth2Authenticator.initialize();
            Session session = OAuth2Authenticator.getSmtpSession(accessToken, true);
            SMTPTransport transport = OAuth2Authenticator.connectToSmtp("smtp.gmail.com", 587, userName, session);

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(userName));
            msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse("kobzeff.inc@mail.ru"));
            msg.setSubject("some subject");
            msg.setText(_emailBody);
            transport.send(msg);
        } catch (Exception ex){
            ex.printStackTrace(System.out);
        }

    }

}
