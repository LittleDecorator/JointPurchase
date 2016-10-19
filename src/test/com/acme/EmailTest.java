package com.acme;

import com.acme.constant.Constants;
import com.acme.model.Content;
import com.acme.repository.ContentRepository;
import com.acme.service.EmailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mail.javamail.JavaMailSender;
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

    String orderTemplate ="<html style=\"line-height: 1.5;font-weight: normal;font-size: 15px;\">\n" +
            "  <head>\n" +
            "\t<title></title>\n" +
            "  </head>\n" +
            "  <body class=\"question-page new-topbar\" style=\"font-family: Ubuntu Condensed;letter-spacing: 0.5pt;color: #846C63;background-color: rgb(246, 244, 241);\">\n" +
            "    <div style=\"margin-top: 25px;padding-bottom: 30px;width: 85%;margin: auto;max-width: 1280px;\">\n" +
            "      <div style=\"padding: 16px;box-sizing: border-box;display: flex;flex-direction: column;margin: 8px;box-shadow: 0 1px 3px 0 rgba(0,0,0,.2),0 1px 1px 0 rgba(0,0,0,.14),0 2px 1px -1px rgba(0,0,0,.12);background-color: rgb(255,255,255);border-radius: 2px;\">\n" +

            "        <div style=\"display: block;position: relative;overflow: auto;color: rgba(0,0,0,0.87);background-color: rgba(76, 175, 80, 0.18);min-height: 100px;\"></div>\n" +

            "        <div style=\"display: block; position: relative; overflow: auto; color: rgba(0,0,0,0.87); background-color: rgb(255,255,255);border-left: 1px solid rgba(221, 221, 221, 0.52);border-right: 1px solid rgba(221, 221, 221, 0.52);\">\n" +
            "          <div layout=\"column\" style=\"flex-direction: column;box-sizing: border-box;display: flex;margin-top: 20px;\">\n" +
            "            <h5 style=\"text-align: center;text-transform: uppercase;margin-bottom: 15px;font-size: 1.5rem;margin: 0.82rem 0 0.656rem 0;font-weight: 400;line-height: 1.1;color: #846C63;\">Ваш заказ \n" +
            "             <span style=\"text-decoration: underline;box-sizing: inherit;\">#1472199774971</span>\n" +
            "             успешно добавлен в обработку!</h5>\n" +
            "            <hr style=\"width: 100px; margin-bottom: 35px;\">\n" +
            "            <span style=\"text-align: center;\">На вашу почту была выслана информация о заказе!</span>\n" +
            "            <span style=\"text-align: center;\" ng-if=\"auth.isAuth()\">Состояние заказа вы можете отслеживать в "+Constants.CABINET_LINK+"</span>\n" +
            "          </div>\n" +

            "<div layout=\"column\" style=\"flex-direction: column;box-sizing: border-box;display: flex;padding-top: 50px;\">\n" +
            "            <h5 style=\"text-align: center;text-transform: uppercase;margin-bottom: 15px;font-size: 1.5rem;margin: 0.82rem 0 0.656rem 0;font-weight: 400;line-height: 1.1;color: #846C63;\">Детализация заказа</h5>\n" +
            "            <hr style=\"width: 100px; margin-bottom: 35px;\">\n" +
            "            <div>\n" +
            "  <div style=\"-webkit-box-flex: 1;flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;-webkit-box-direction: normal;-webkit-box-orient: vertical;flex-direction: column;box-sizing: border-box;display: flex;text-align: center;\">\n" +
            "  <p style=\"line-height: 2.6em;font-size: 16px;font-weight: 500;letter-spacing: .01em;margin: 0;\">Дата заказа: {{orderDate}} Способ доставки: {{orderDelivery}} Сумма к оплате: {{orderPayment}}</p>\n" +
            "  <hr style=\"width: 100px; margin-bottom: 35px;\">\n" +
            "</div>\n" +
            "              <div style=\"display: block;width: 80%;margin: auto;border: 1px solid #ddd;border-radius: 5px;margin-bottom: 20px;\">\n" +
            "  <div style=\"position: relative;height: auto;min-height: 88px;-webkit-box-align: start;align-items: flex-start;-webkit-box-pack: center;justify-content: center;display: flex;\">\n" +
            "  \t<div style=\"width: 100%;height: auto;min-height: 88px;padding: 0 16px;margin: 0;font-weight: 400;border: medium none;display: flex;align-items: center;\">\n" +
            "  \t\t<img src=\"cid:image\" class=\"md-avatar\" alt=\"item_image\" style=\"-webkit-box-flex: 0;flex: none;width: 80px;height: 80px;margin-top: 8px;margin-bottom: 8px;margin-right: 16px;border-radius: 50%;box-sizing: content-box;\">\n" +
            "  \t\t<div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
            "  \t\t\t<p style=\"font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">{{itemName}}</p>\n" +
            "\t\t</div>\n" +
            "      \t<div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
            "  \t\t\t<p style=\"font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">{{itemCost}}</p>\n" +
            "\t\t</div>\n" +
            "  \t\t<div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
            "  \t\t\t<p style=\"font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">{{itemCount}}</p>\n" +
            "\t\t</div>\n" +
            "  \t</div>\n" +
            "  </div>\n" +
            "  <div style=\"position: relative;height: auto;min-height: 88px;align-items: flex-start;justify-content: center;display: flex;\">\n" +
            "    <div style=\"width: 100%;height: auto;min-height: 88px;justify-content: flex-start;padding: 0 16px;margin: 0;font-weight: 400;text-align: left;border: medium none;display: flex;align-items: center;\">\n" +
            "  \t\t<img src=\"cid:image\" class=\"md-avatar\" alt=\"item_image\" style=\"-webkit-box-flex: 0;flex: none;width: 80px;height: 80px;margin-top: 8px;margin-bottom: 8px;margin-right: 16px;border-radius: 50%;box-sizing: content-box;\">\n" +
            "  \t\t<div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
            "  \t\t\t<p style=\"font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">{{itemName}}</p>\n" +
            "\t\t</div>\n" +
            "      \t<div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
            "  \t\t\t<p style=\"font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">{{itemCost}}</p>\n" +
            "\t\t</div>\n" +
            "  \t\t<div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
            "  \t\t\t<p style=\"font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">{{itemCount}}</p>\n" +
            "\t\t</div>\n" +
            "      <div>\n" +
            "  </div>\n" +
            "</div></div></div>\n" +
            "              \n" +
            "              \n" +
            "  \t\t\t</div>\n" +
            "          </div>" +

            "       </div>\n" +
            "       <div style=\"display: block;position: relative;overflow: auto;color: rgba(0,0,0,0.87);background-color: rgba(76, 175, 80, 0.18);min-height: 100px;\"></div>" +
            "       </div>"+
            "   </div>"+
            " </body>"+
            "</html>";


/* Comment as old working approach. It correct? but we use spring helper */
//    @Test
//    public void sendOrder() throws MessagingException {
//        EmailBuilder builder = emailService.getBuilder(emailService.createSession("knpdeveloper@gmail.com", "25oct87!"));
//        try{
//            MimeMessage message = builder.setTo("npkobzev@mail.ru").setFrom("knpdeveloper@gmail.com").setHtmlContent(orderTemplate).setSubject("test subject").build();
//            emailService.send(message);
//        } catch (MessagingException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }

//    @Test
//    //TODO: don't redirect to record and cabinet after login
//    public void sendOrderConformationHtml() {
//        MimeMessage mail = mailSender.createMimeMessage();
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
//            helper.setTo("npkobzev@mail.ru");
////            helper.setReplyTo("someone@localhost");
//            helper.setFrom("knpdeveloper@gmail.com");
//            helper.setSubject("Some test Subject");
//            helper.setText(orderTemplate, true);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//        mailSender.send(mail);
//    }


    @Test
    public void sendOrderConformationWithImageHtml() throws MessagingException {
        Content content = contentRepository.getById("d54be40a-143e-4a7f-8a18-a234b30d7c82");
        Multipart multipart = new MimeMultipart("related");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo("npkobzev@mail.ru");
        helper.setFrom("robot.grimmstory@gmail.com");
        helper.setSubject("Your order");


        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(orderTemplate, "text/html; charset=utf-8");
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
