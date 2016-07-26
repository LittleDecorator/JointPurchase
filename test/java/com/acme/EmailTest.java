package com.acme;

import com.acme.model.Content;
import com.acme.repository.ContentRepository;
import com.acme.service.EmailService;
import com.acme.util.EmailBuilder;
import com.sun.mail.smtp.SMTPMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.UnsupportedEncodingException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
@TestPropertySource(locations="classpath:test.properties")
@IntegrationTest("server.port:0")
public class EmailTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    JavaMailSender mailSender;


    @Test
    public void sendText() throws MessagingException {
        EmailBuilder builder = emailService.getBuilder(emailService.createSession("knpdeveloper@gmail.com", "25oct87!"));
        try{
            MimeMessage message = builder.setTo("npkobzev@mail.ru").setFrom("knpdeveloper@gmail.com").setHtmlContent("TEST").setSubject("test subject").build();
            emailService.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void sendImage() throws MessagingException {
        Content content = contentRepository.getById("6f6ab50d-7d5f-470c-8a9d-5b7cf12a7548");
        Multipart multipart = new MimeMultipart("related");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo("npkobzev@mail.ru");
        helper.setFrom("knpdeveloper@gmail.com");
        helper.setSubject("Some test Subject");


        BodyPart messageBodyPart = new MimeBodyPart();
        String htmlText = "<H1>Hello</H1><img src='cid:image'>";
        messageBodyPart.setContent(htmlText, "text/html");
        multipart.addBodyPart(messageBodyPart);

        messageBodyPart = new MimeBodyPart();
        ByteArrayDataSource dataSource = new ByteArrayDataSource( content.getContent(), content.getMime() );

        messageBodyPart.setDataHandler(new DataHandler(dataSource));
        messageBodyPart.setHeader("Content-ID", "<image>");

        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);

        mailSender.send(message);
    }

}
