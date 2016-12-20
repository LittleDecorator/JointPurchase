package com.acme.service.impl;

import com.acme.constant.Constants;
import com.acme.email.Email;
import com.acme.email.ImageType;
import com.acme.email.InlinePicture;
import com.acme.email.impl.EmailAttachmentImpl;
import com.acme.email.impl.EmailImpl;
import com.acme.email.impl.InlinePictureImpl;
import com.acme.exception.EmailConversionException;
import com.acme.exception.TemplateException;
import com.acme.model.Content;
import com.acme.model.Item;
import com.acme.model.OrderItem;
import com.acme.model.PurchaseOrder;
import com.acme.service.EmailService;
import com.acme.service.OrderService;
import com.acme.service.SubjectService;
import com.acme.service.TemplateService;
import com.acme.util.EmailBuilder;
import com.acme.util._EmailBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Optional.fromNullable;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Service
//@PropertySource("classpath:mail.properties")
@Slf4j
public class EmailServiceImpl implements EmailService {

//    @Autowired
//    Properties mailProperties;

    @Value("${app.name}")
    private String senderName;

    @Value("${spring.mail.username}")
    private String senderAddress;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private TemplateService templateService;


    public void sendOrderDone(String mailTo){
//        EmailBuilder builder = getBuilder(createSession("bobby", "12345678"));
//        try{
//            MimeMessage message = builder.setTo(mailTo).setFrom(getRobotCredential()).setHtmlContent(Constants.ORDER_CREATE).setSubject("Your purchase accepted").build();
//            send(message);
//        } catch (MessagingException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public boolean sendRegistrationToken(String mailTo,String content) {
//        EmailBuilder builder = getBuilder(createSession("bobby", "12345678"));
//        try{
//            MimeMessage message = builder.setTo(mailTo).setFrom(getRobotCredential()).setHtmlContent(content).setSubject("Registration confirmation").build();
//            send(message);
//        } catch (MessagingException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
        return false;
    }

//    @Override
//    public List<Email> getInboxEmail() throws MessagingException {
//        List<Email> emails = null;
//        MimeMessage msg;
//        Email email;
//
//        Store store = createSession("bobby", "12345678").getStore("pop3");
//        store.connect("80.78.247.250", null, null);
//        Folder inbox = store.getFolder("INBOX");
//
//        inbox.open(Folder.READ_ONLY);
//        int cou = inbox.getMessageCount();
//        if(cou>0){
//            emails = new ArrayList<>();
//            for(int i=1;i<=cou;i++){
//                msg = (MimeMessage) inbox.getMessage(i);
//                email = new Email();
//
//                String subFrom = Arrays.toString(msg.getFrom());
//                subFrom = subFrom.substring(subFrom.indexOf('<')+1,subFrom.length()-2);
//                email.setId(msg.getMessageID());
//                email.setFrom(subFrom);
//                email.setDate(msg.getSentDate());
//                email.setIsNew(msg.isSet(Flags.Flag.RECENT));
//                email.setSubject(msg.getSubject());
//                emails.add(email);
//            }
//        }
//        System.out.println(emails);
//        return emails;
//    }

//    @Override
//    public List<Email> getSendEmail() throws MessagingException {
//        List<Email> emails = null;
//        MimeMessage msg;
//        Email email;
//
//        Store store = createSession("bobby", "12345678").getStore("pop3");
//        store.connect("80.78.247.250", null, null);
//        Folder inbox = store.getFolder("SEND");
//
//        inbox.open(Folder.READ_ONLY);
//        int cou = inbox.getMessageCount();
//        if(cou>0){
//            emails = new ArrayList<>();
//            for(int i=1;i<=cou;i++){
//                msg = (MimeMessage) inbox.getMessage(i);
//                email = new Email();
//                email.setId(msg.getMessageID());
//                email.setTo(Arrays.asList(msg.getRecipients(Message.RecipientType.TO)).toString());
//                email.setDate(msg.getSentDate());
//                email.setSubject(msg.getSubject());
//                emails.add(email);
//            }
//        }
//        System.out.println(emails);
//        return emails;
//    }

    public void sendOrderAccepted(PurchaseOrder order) throws IOException, MessagingException, TemplateException {
        EmailBuilder builder = EmailBuilder.getBuilder();

        /* get all info */
        Map<String, Object> info = orderService.getOrderInfo(order.getId());
        Map<String, OrderItem> orderItemMap = (Map<String, OrderItem>) info.get("orderItems");
        Map<String, Item> itemMap = (Map<String, Item>) info.get("items");
        Map<String, Content> contentMap = (Map<String, Content>) info.get("contents");
        List<Map<String, String>> list = Lists.newArrayList();

        /* перебираем товар */
        itemMap.keySet().forEach(itemId -> list.add(new ImmutableMap.Builder<String, String>()
						.put("imageName", contentMap.get(itemId).getId())
						.put("itemName", itemMap.get(itemId).getName())
						.put("itemCost", itemMap.get(itemId).getPrice()+ " руб")
						.put("itemCount", orderItemMap.get(itemId).getCou() + " шт")
				.build()));


        /* build template data */
        final Map<String, Object> data = new ImmutableMap.Builder<String, Object>()
				/* основной текст письма */
                .put("order_number", order.getUid())
                .put("isAuth", true)
                .put("cabinet_link", Constants.CABINET_LINK)
				/* информация о заказе */
                .put("orderDate", order.getDateAdd() == null ? new Date() : order.getDateAdd())
                .put("orderDelivery", order.getDelivery())
                .put("orderPayment", order.getPayment()+ " руб")
				/* данные о товаре */
                .put("item", list)
                .build();

        MimeMessage message = builder.setMessage(convert(createEmail(order)))
                .setInlinePictures(collectPics(Lists.newArrayList(contentMap.values())))
                .setEmailContent(templateService.mergeTemplateIntoString(Constants.ORDER_EMAIL_TEMPLATE, data))
                .build();
        mailSender.send(message);
    }

    /**
     * Чтение шаблона email
     * @param file
     * @return
     * @throws IOException
     */
    private String readFile(final File file) throws IOException {
        final byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, Charset.forName("UTF-8"));
    }

    /**
     * Чтение изображения из ресурсов
     * @param imageName
     * @return
     */
    private File readImg(String imageName){
        final File inlineImageFile = new File(getClass().getClassLoader()
                .getResource("img" + File.separator + imageName).getFile());
        return inlineImageFile;
    }

    /**
     * Чтение изображения из ресурсов
     * @param imageName
     * @return
     */
    private byte[] readImgAsByte(String imageName) throws IOException {
        return  ByteStreams.toByteArray(getClass().getClassLoader().getResourceAsStream("img" + File.separator + imageName));
    }

    /**
     * Преобразуем наш email в MimeMessage
     * @return
     */
    private MimeMessage convert(Email email){
        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,
                fromNullable(email.getEncoding()).or(Charset.forName("UTF-8")).displayName());

        try {
            messageHelper.setFrom(email.getFrom());
            if (ofNullable(email.getReplyTo()).isPresent()) {
                messageHelper.setReplyTo(email.getReplyTo());
            }
            if (ofNullable(email.getTo()).isPresent()) {
                for (final InternetAddress address : email.getTo()) {
                    messageHelper.addTo(address);
                }
            }
            if (ofNullable(email.getCc()).isPresent()) {
                for (final InternetAddress address : email.getCc()) {
                    messageHelper.addCc(address);
                }
            }
            if (ofNullable(email.getBcc()).isPresent()) {
                for (final InternetAddress address : email.getBcc()) {
                    messageHelper.addBcc(address);
                }
            }
            if (ofNullable(email.getAttachments()).isPresent()) {
                for (final EmailAttachmentImpl attachment : email.getAttachments()) {
                    try {
                        messageHelper.addAttachment(attachment.getAttachmentName(),
                                attachment.getInputStream(), attachment.getContentType().getType());
                    } catch (IOException e) {
                        log.error("Error while converting Email to MimeMessage");
                        throw new EmailConversionException(e);
                    }
                }
            }
            messageHelper.setSubject(ofNullable(email.getSubject()).orElse(""));
            messageHelper.setText(ofNullable(email.getBody()).orElse(""));

            if (nonNull(email.getSentAt())) {
                messageHelper.setSentDate(email.getSentAt());
            }
        } catch (MessagingException e) {
            log.error("Error while converting Email to MimeMessage");
            throw new EmailConversionException(e);
        }

        return mimeMessage;
    }

    private Email createEmail(PurchaseOrder order) throws UnsupportedEncodingException {
        return EmailImpl.builder()
                .from(new InternetAddress(senderAddress, senderName))
                .to(Lists.newArrayList(new InternetAddress(order.getRecipientEmail(), order.getRecipientFname())))
                .subject("Thanks for your order, "+order.getRecipientFname())
                .sentAt(new Date())
                .body("")
                .encoding(Charset.forName("UTF-8"))
                .build();
    }

    /**
     * Создадим обертки для изображений
     * @param contents
     * @return
     */
    private List<InlinePicture> collectPics(List<Content> contents) throws IOException {
        List<InlinePicture> pictures = Lists.newArrayList();
        pictures.addAll(contents.stream().map(content -> InlinePictureImpl.builder()
                .content(content.getContent())
                .imageType(ImageType.valueOf(content.getType().toUpperCase()))
                .templateName(content.getId()).build()).collect(Collectors.toList()));
        return pictures;
    }

}
