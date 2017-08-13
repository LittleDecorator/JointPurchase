package com.acme;

import com.acme.constant.Constants;
import com.acme.handlers.Base64BytesSerializer;
import com.acme.model.Content;
import com.acme.model.Order;
import com.acme.repository.ContentRepository;
import com.acme.repository.OrderRepository;
import com.acme.service.EmailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@TestPropertySource(locations="classpath:application.properties")
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmailTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private JavaMailSender mailSender;

    String orderTemplate ="<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\" \"http://www.w3.org/TR/REC-html40/loose.dtd\">\n" +
                     "<html style=\"line-height: 1.5;font-weight: normal;font-size: 15px;\">\n" +
                     "  <head>\n" +
                     "    <title></title>\n" +
                     "  </head>\n" +
                     "  <body style=\"Margin:0;padding:0;min-width:100%;background-color:#ffffff;font-family: Ubuntu Condensed;letter-spacing: 0.5pt;color: #846C63;background-color: rgb(246, 244, 241);\">\n" +
                     "    <div style=\"display: block;position: relative;overflow: auto;color: rgba(0,0,0,0.87);background-color: rgba(76, 175, 80, 0.18);min-height: 100px;\"></div>\n" +
                     "    <div style=\"max-width:1280px;display: block; position: relative; overflow: auto; color: rgba(0,0,0,0.87); background-color: rgb(255,255,255);border-left: 1px solid rgba(221, 221, 221, 0.52);border-right: 1px solid rgba(221, 221, 221, 0.52);\">\n" +
                     "      <table align=\"center\" style=\"border-spacing:0;color:#333333;Margin:0 auto;width:100%;max-width:1280px;\">\n" +
                     "        <tr>\n" +
                     "          <td style=\"padding:0;\">\n" +
                     "            <table width=\"100%\" style=\"border-spacing:0;color:#333333;\">\n" +
                     "              <tr>\n" +
                     "                <td style=\"padding:0;padding:10px;width:100%;text-align:center;\">\n" +
                     "                  <h5 style=\"text-align: center;text-transform: uppercase;margin-bottom: 15px;font-size: 1.5rem;margin: 0.82rem 0 0.656rem 0;font-weight: 400;line-height: 1.1;color: #846C63;\">\n" +
                     "                    Ваш заказ\n" +
                     "                                <span style=\"text-decoration: underline;box-sizing: inherit;\">#1472199774971</span>\n" +
                     "                    успешно добавлен в обработку!</h5>\n" +
                     "                  <hr style=\"width: 100px;\">\n" +
                     "                  <span style=\"text-align: center;\" ng-if=\"auth.isAuth()\">Состояние заказа вы можете отслеживать в "+Constants.CABINET_LINK+"</span>\n" +
                     "                  <hr style=\"width: 100px;\">\n" +
                     "                </td>\n" +
                     "              </tr>\n" +
                     "            </table>\n" +
                     "          </td>\n" +
                     "        </tr>\n" +
                     "        <tr>\n" +
                     "          <td style=\"padding:0;text-align:center;font-size:0;padding-top:10px;padding-bottom:10px;padding: 0;\">\n" +
                     "            <table style=\"border-spacing:0;color:#333333;width:100%;max-width:315px;display:inline-table;vertical-align:top;\">\n" +
                     "              <tr>\n" +
                     "                <td style=\"padding:0;padding:10px;width:100%;font-size:14px;text-align:center;padding: 0;\">\n" +
                     "                  <p style=\"Margin:0;line-height: 2.6em;font-size: 16px;font-weight: 500;letter-spacing: .01em;margin: 0;\">\n" +
                     "                    Дата заказа: $orderDate\n" +
                     "                            </p>\n" +
                     "                </td>\n" +
                     "              </tr>\n" +
                     "            </table>\n" +
                     "            <table style=\"border-spacing:0;color:#333333;width:100%;max-width:315px;display:inline-table;vertical-align:top;\">\n" +
                     "              <tr>\n" +
                     "                <td style=\"padding:0;padding:10px;width:100%;font-size:14px;text-align:center;padding: 0;\">\n" +
                     "                  <p style=\"Margin:0;line-height: 2.6em;font-size: 16px;font-weight: 500;letter-spacing: .01em;margin: 0;\">\n" +
                     "                    Способ доставки: $orderDelivery\n" +
                     "                            </p>\n" +
                     "                </td>\n" +
                     "              </tr>\n" +
                     "            </table>\n" +
                     "            <table style=\"border-spacing:0;color:#333333;width:100%;max-width:315px;display:inline-table;vertical-align:top;\">\n" +
                     "              <tr>\n" +
                     "                <td style=\"padding:0;padding:10px;width:100%;font-size:14px;text-align:center;padding: 0;\">\n" +
                     "                  <p style=\"Margin:0;line-height: 2.6em;font-size: 16px;font-weight: 500;letter-spacing: .01em;margin: 0;\">\n" +
                     "                    Сумма к оплате: $orderPayment\n" +
                     "                            </p>\n" +
                     "                </td>\n" +
                     "              </tr>\n" +
                     "            </table>\n" +
                     "          </td>\n" +
                     "        </tr>\n" +
                     "        <tr>\n" +
                     "          <td style=\"padding:0;\">\n" +
                     "            <table width=\"100%\" style=\"border-spacing:0;color:#333333;\">\n" +
                     "              <tr>\n" +
                     "                <td style=\"padding:0;padding:10px;width:100%;text-align:center;\">\n" +
                     "                  <h5 style=\"text-align: center;text-transform: uppercase;margin-bottom: 15px;font-size: 1.2rem;margin: 0.82rem 0 0.656rem 0;font-weight: 400;line-height: 1.1;color: #846C63;\">\n" +
                     "                    Детализация заказа</h5>\n" +
                     "                  <hr style=\"width: 100px; margin-bottom: 5px;\">\n" +
                     "                </td>\n" +
                     "              </tr>\n" +
                     "            </table>\n" +
                     "          </td>\n" +
                     "        </tr>\n" +
                     "        <tr>\n" +
                     "          <td style=\"padding:0;text-align:center;font-size:0;padding-top:10px;padding-bottom:10px;\">\n" +
                     "            <table style=\"border-spacing:0;color:#333333;width:100%;max-width:315px;display:inline-table;vertical-align:top;\">\n" +
                     "              <tr>\n" +
                     "                <td style=\"padding:0;padding:10px;width:100%;font-size:14px;text-align:center;\">\n" +
                     "                  <div style=\"padding: 0 10px;display: flex;\">\n" +
                     "                    <div style=\"float:left;position:relative;width:20%;box-shadow:0 1px 3px 0 rgba(0,0,0,.2),0 1px 1px 0 rgba(0,0,0,.14),0 2px 1px -1px rgba(0,0,0,.12);margin:3px;background-color:white;width: 90px; height: 90px; border-radius: 50%; overflow: hidden;padding-bottom:0;\">\n" +
                     "                      <img src=\"cid:image\" style=\"width: 90px;height: 90px;\">\n" +
                     "                    </div>\n" +
                     "                    <table style=\"border-spacing:0;color:#333333;width: inherit;margin: auto auto auto 10px;\">\n" +
                     "                      <tr>\n" +
                     "                        <td style=\"padding:0;\">\n" +
                     "                          <table width=\"100%\" style=\"border-spacing:0;color:#333333;\">\n" +
                     "                            <tr>\n" +
                     "                              <td style=\"padding:0;width:100%;font-size:14px;text-align:center;padding: 3px;\">\n" +
                     "                                <div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
                     "                                  <p style=\"Margin:0;font-size:14px;Margin-bottom:10px;font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">$itemName</p>\n" +
                     "                                </div>\n" +
                     "                              </td>\n" +
                     "                            </tr>\n" +
                     "                            <tr>\n" +
                     "                              <td style=\"padding:0;width:100%;font-size:14px;text-align:center;padding: 3px;\">\n" +
                     "                                <div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
                     "                                  <p style=\"Margin:0;font-size:14px;Margin-bottom:10px;font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">$itemCost</p>\n" +
                     "                                </div>\n" +
                     "                              </td>\n" +
                     "                            </tr>\n" +
                     "                            <tr>\n" +
                     "                              <td style=\"padding:0;width:100%;font-size:14px;text-align:center;padding: 3px;\">\n" +
                     "                                <div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
                     "                                  <p style=\"Margin:0;font-size:14px;Margin-bottom:10px;font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">$itemCount</p>\n" +
                     "                                </div>\n" +
                     "                              </td>\n" +
                     "                            </tr>\n" +
                     "                          </table>\n" +
                     "                        </td>\n" +
                     "                      </tr>\n" +
                     "                    </table>\n" +
                     "                  </div>\n" +
                     "                </td>\n" +
                     "              </tr>\n" +
                     "            </table>\n" +
                     "            <table style=\"border-spacing:0;color:#333333;width:100%;max-width:315px;display:inline-table;vertical-align:top;\">\n" +
                     "              <tr>\n" +
                     "                <td style=\"padding:0;padding:10px;width:100%;font-size:14px;text-align:center;\">\n" +
                     "                  <div style=\"padding: 0 10px;display: flex;\">\n" +
                     "                    <div style=\"float:left;position:relative;width:20%;box-shadow:0 1px 3px 0 rgba(0,0,0,.2),0 1px 1px 0 rgba(0,0,0,.14),0 2px 1px -1px rgba(0,0,0,.12);margin:3px;background-color:white;width: 90px; height: 90px; border-radius: 50%; overflow: hidden;padding-bottom:0;\">\n" +
                     "                      <img src=\"cid:image\" style=\"width: 90px;height: 90px;\">\n" +
                     "                    </div>\n" +
                     "                    <table style=\"border-spacing:0;color:#333333;width: inherit;margin: auto auto auto 10px;\">\n" +
                     "                      <tr>\n" +
                     "                        <td style=\"padding:0;\">\n" +
                     "                          <table width=\"100%\" style=\"border-spacing:0;color:#333333;\">\n" +
                     "                            <tr>\n" +
                     "                              <td style=\"padding:0;width:100%;font-size:14px;text-align:center;padding: 3px;\">\n" +
                     "                                <div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
                     "                                  <p style=\"Margin:0;font-size:14px;Margin-bottom:10px;font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">$itemName</p>\n" +
                     "                                </div>\n" +
                     "                              </td>\n" +
                     "                            </tr>\n" +
                     "                            <tr>\n" +
                     "                              <td style=\"padding:0;width:100%;font-size:14px;text-align:center;padding: 3px;\">\n" +
                     "                                <div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
                     "                                  <p style=\"Margin:0;font-size:14px;Margin-bottom:10px;font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">$itemCost</p>\n" +
                     "                                </div>\n" +
                     "                              </td>\n" +
                     "                            </tr>\n" +
                     "                            <tr>\n" +
                     "                              <td style=\"padding:0;width:100%;font-size:14px;text-align:center;padding: 3px;\">\n" +
                     "                                <div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
                     "                                  <p style=\"Margin:0;font-size:14px;Margin-bottom:10px;font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">$itemCount</p>\n" +
                     "                                </div>\n" +
                     "                              </td>\n" +
                     "                            </tr>\n" +
                     "                          </table>\n" +
                     "                        </td>\n" +
                     "                      </tr>\n" +
                     "                    </table>\n" +
                     "                  </div>\n" +
                     "                </td>\n" +
                     "              </tr>\n" +
                     "            </table>\n" +
                     "            <table style=\"border-spacing:0;color:#333333;width:100%;max-width:315px;display:inline-table;vertical-align:top;\">\n" +
                     "              <tr>\n" +
                     "                <td style=\"padding:0;padding:10px;width:100%;font-size:14px;text-align:center;\">\n" +
                     "                  <div style=\"padding: 0 10px;display: flex;\">\n" +
                     "                    <div style=\"float:left;position:relative;width:20%;box-shadow:0 1px 3px 0 rgba(0,0,0,.2),0 1px 1px 0 rgba(0,0,0,.14),0 2px 1px -1px rgba(0,0,0,.12);margin:3px;background-color:white;width: 90px; height: 90px; border-radius: 50%; overflow: hidden;padding-bottom:0;\">\n" +
                     "                      <img src=\"cid:image\" style=\"width: 90px;height: 90px;\">\n" +
                     "                    </div>\n" +
                     "                    <table style=\"border-spacing:0;color:#333333;width: inherit;margin: auto auto auto 10px;\">\n" +
                     "                      <tr>\n" +
                     "                        <td style=\"padding:0;\">\n" +
                     "                          <table width=\"100%\" style=\"border-spacing:0;color:#333333;\">\n" +
                     "                            <tr>\n" +
                     "                              <td style=\"padding:0;width:100%;font-size:14px;text-align:center;padding: 3px;\">\n" +
                     "                                <div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
                     "                                  <p style=\"Margin:0;font-size:14px;Margin-bottom:10px;font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">$itemName</p>\n" +
                     "                                </div>\n" +
                     "                              </td>\n" +
                     "                            </tr>\n" +
                     "                            <tr>\n" +
                     "                              <td style=\"padding:0;width:100%;font-size:14px;text-align:center;padding: 3px;\">\n" +
                     "                                <div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
                     "                                  <p style=\"Margin:0;font-size:14px;Margin-bottom:10px;font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">$itemCost</p>\n" +
                     "                                </div>\n" +
                     "                              </td>\n" +
                     "                            </tr>\n" +
                     "                            <tr>\n" +
                     "                              <td style=\"padding:0;width:100%;font-size:14px;text-align:center;padding: 3px;\">\n" +
                     "                                <div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
                     "                                  <p style=\"Margin:0;font-size:14px;Margin-bottom:10px;font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">$itemCount</p>\n" +
                     "                                </div>\n" +
                     "                              </td>\n" +
                     "                            </tr>\n" +
                     "                          </table>\n" +
                     "                        </td>\n" +
                     "                      </tr>\n" +
                     "                    </table>\n" +
                     "                  </div>\n" +
                     "                </td>\n" +
                     "              </tr>\n" +
                     "            </table>\n" +
                     "            <table style=\"border-spacing:0;color:#333333;width:100%;max-width:315px;display:inline-table;vertical-align:top;\">\n" +
                     "              <tr>\n" +
                     "                <td style=\"padding:0;padding:10px;width:100%;font-size:14px;text-align:center;\">\n" +
                     "                  <div style=\"padding: 0 10px;display: flex;\">\n" +
                     "                    <div style=\"float:left;position:relative;width:20%;box-shadow:0 1px 3px 0 rgba(0,0,0,.2),0 1px 1px 0 rgba(0,0,0,.14),0 2px 1px -1px rgba(0,0,0,.12);margin:3px;background-color:white;width: 90px; height: 90px; border-radius: 50%; overflow: hidden;padding-bottom:0;\">\n" +
                     "                      <img src=\"cid:image\" style=\"width: 90px;height: 90px;\">\n" +
                     "                    </div>\n" +
                     "                    <table style=\"border-spacing:0;color:#333333;width: inherit;margin: auto auto auto 10px;\">\n" +
                     "                      <tr>\n" +
                     "                        <td style=\"padding:0;\">\n" +
                     "                          <table width=\"100%\" style=\"border-spacing:0;color:#333333;\">\n" +
                     "                            <tr>\n" +
                     "                              <td style=\"padding:0;width:100%;font-size:14px;text-align:center;padding: 3px;\">\n" +
                     "                                <div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
                     "                                  <p style=\"Margin:0;font-size:14px;Margin-bottom:10px;font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">$itemName</p>\n" +
                     "                                </div>\n" +
                     "                              </td>\n" +
                     "                            </tr>\n" +
                     "                            <tr>\n" +
                     "                              <td style=\"padding:0;width:100%;font-size:14px;text-align:center;padding: 3px;\">\n" +
                     "                                <div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
                     "                                  <p style=\"Margin:0;font-size:14px;Margin-bottom:10px;font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">$itemCost</p>\n" +
                     "                                </div>\n" +
                     "                              </td>\n" +
                     "                            </tr>\n" +
                     "                            <tr>\n" +
                     "                              <td style=\"padding:0;width:100%;font-size:14px;text-align:center;padding: 3px;\">\n" +
                     "                                <div style=\"flex: 1 1 auto;margin: auto;text-overflow: ellipsis;overflow: hidden;flex-direction: column;box-sizing: border-box;text-align: center;\">\n" +
                     "                                  <p style=\"Margin:0;font-size:14px;Margin-bottom:10px;font-size: 14px;font-weight: 500;letter-spacing: .01em;margin: 0;line-height: 1.6em;\">$itemCount</p>\n" +
                     "                                </div>\n" +
                     "                              </td>\n" +
                     "                            </tr>\n" +
                     "                          </table>\n" +
                     "                        </td>\n" +
                     "                      </tr>\n" +
                     "                    </table>\n" +
                     "                  </div>\n" +
                     "                </td>\n" +
                     "              </tr>\n" +
                     "            </table>\n" +
                     "          </td>\n" +
                     "        </tr>\n" +
                     "      </table>\n" +
                     "    </div>\n" +
                     "    <div style=\"display: block;position: relative;overflow: auto;color: rgba(0,0,0,0.87);background-color: rgba(76, 175, 80, 0.18);min-height: 100px;\"></div>\n" +
                     "  </body>\n" +
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


    @Test
    public void sendOrderStatus() throws MessagingException {
        try{
            Order order = orderRepository.findOne("ff8081815be46325015be463a0a40000");
            order.setRecipientEmail("kobzeff.inc@mail.ru");
            emailService.sendOrderStatus(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
    public void sendOrderConformationWithImageHtml() throws MessagingException, IOException {
        Content content = contentRepository.findOneByIsDefault(true);
        Multipart multipart = new MimeMultipart("related");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
//        helper.setTo("npkobzev@mail.ru");
        helper.setTo("kobzeff.inc@mail.ru");
        helper.setFrom(new InternetAddress("robot.grimmstory@gmail.com","GrimmStory"));
        helper.setSubject("Your order");

        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(orderTemplate, "text/html; charset=utf-8");

        multipart.addBodyPart(messageBodyPart);

        messageBodyPart = new MimeBodyPart();
        ByteArrayDataSource dataSource = new ByteArrayDataSource( Base64BytesSerializer.deserialize(content.getContent()), content.getMime() );

        messageBodyPart.setDataHandler(new DataHandler(dataSource));
        messageBodyPart.setHeader("Content-ID", "<image>");

        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);

        mailSender.send(message);
    }

}
