package com.acme.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Configuration
@PropertySource("classpath:mail.properties")
public class MailConfig {

//    @Value("${mail.protocol}")
//    private String protocol;

    @Value("${mail.smtp.host}")
    private String host;

    @Value("${mail.smtp.port}")
    private int port;

    @Value("${mail.smtp.auth}")
    private boolean auth;

    @Value("${mail.debug}")
    private boolean debug;

    @Value("${mail.smtp.starttls.enable}")
    private boolean starttls;

    @Value("${mail.smtp.ssl.trust}")
    private String ssl;

    @Value("${mail.robot.login}")
    private String login;

    @Value("${mail.robot.password}")
    private String password;

    @Value("${mail.store.protocol}")
    private String store;

    @Bean
    public Properties mailProperties(){
        Properties props = new Properties();
        props.put("mail.debug", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.smtp.ssl.trust", ssl);
        props.put("mail.store.protocol", store);
        return props;
    }

    @Bean
    public Session robotMailSession(){
        Properties props = mailProperties();
        System.out.println(props.stringPropertyNames());
        // Get the default Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(getRobotName(), password);
                    }
                });
        return session;
    }

    private String getRobotName(){
        String res = login+"@"+host;
        System.out.println(res);
        return res;
    }
}
