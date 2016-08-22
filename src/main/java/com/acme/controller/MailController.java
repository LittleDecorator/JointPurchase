package com.acme.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
public class MailController {

//    @Autowired
//    private EmailService emailService;

//    @RequestMapping("/inbox")
//    public List<Email> receivedEmail() throws MessagingException {
//        return emailService.getInboxEmail();
//    }
//
//    @RequestMapping("/send")
//    public List<Email> sendEMail() throws MessagingException {
//        return emailService.getSendEmail();
//    }

//    @RequestMapping("/mail/registration/done")
//    @ResponseStatus(HttpStatus.CREATED)
//    public void sendRegistrationDone(String recipient){
//        notifySend(recipient, Constants.REGISTRATION_DONE, "Registration confirmation");
//    }
//
//    @RequestMapping("/mail/order/create")
//    @ResponseStatus(HttpStatus.CREATED)
//    public void sendOrderCreate(String recipient){
//        notifySend(recipient,Constants.ORDER_CREATE,"Order confirmation");
//    }
//
//    private void notifySend(String recipient,String mailContent,String subject) {
//        EmailBuilder builder = emailService.getBuilder(emailService.createSession("bobby", "12345678"));
//        try{
//            MimeMessage message = builder.setTo(recipient).setFrom(emailService.getRobotCredential()).setHtmlContent(mailContent).setSubject(subject).build();
//            emailService.send(message);
//        } catch (MessagingException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//    }

}
