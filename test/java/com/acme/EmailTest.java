package com.acme;

import com.acme.model.Content;
import com.acme.repository.ContentRepository;
import com.acme.service.EmailService;
import com.acme.util.EmailBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
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

//    @Test
//    public void sendText() throws MessagingException {
//        EmailBuilder builder = emailService.getBuilder(emailService.createSession("knpdeveloper@gmail.com", "25oct87!"));
//        try{
//            MimeMessage message = builder.setTo("npkobzev@mail.ru").setFrom("knpdeveloper@gmail.com").setHtmlContent("TEST").setSubject("test subject").build();
//            emailService.send(message);
//        } catch (MessagingException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }


    @Test
    public void sendImage() throws MessagingException {
        Content content = contentRepository.getById("69aabb97-ab23-47c6-84b3-40033179badf");
        Multipart multipart = new MimeMultipart("related");
        EmailBuilder builder = emailService.getBuilder(emailService.createSession("knpdeveloper@gmail.com", "25oct87!"));
        try{
            MimeMessage message = builder.setTo("npkobzev@mail.ru").setFrom("knpdeveloper@gmail.com").setHtmlContent("<html><body><h2>A title</h2>Some text in here<br/>" +
                    "<img src=\"cid:" + content.getId() + "\"/><br/> some more text<img src=\"cid:" + content.getId() + "\"/></body></html>").setSubject("test subject").
                    setAttachment(content.getContent(), content.getMime(), content.getId()).build();
            emailService.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
