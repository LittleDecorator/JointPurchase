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
import com.acme.handlers.Base64BytesSerializer;
import com.acme.model.*;
import com.acme.service.EmailService;
import com.acme.service.OrderService;
import com.acme.service.SubjectService;
import com.acme.service.TemplateService;
import com.acme.util.EmailBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;
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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Optional.fromNullable;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

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

    @Override
    public boolean sendRegistrationToken(String mailTo, String tokenLink) {
        boolean result = true;

        try{
            EmailBuilder builder = EmailBuilder.getBuilder();
            /* получаем клиента */
            Subject subject = subjectService.getSubjectByEmail(mailTo);
            /* берем его полное имя */
            String subjectFullName = subject.getLastName() + " "+ subject.getFirstName();
            /* Получим объект сообщения */
            Email email = EmailImpl.builder()
                    .from(new InternetAddress(senderAddress, senderName))
                    .to(Lists.newArrayList(new InternetAddress(subject.getEmail(), subjectFullName)))
                    .subject("Registration confirmation")
                    .sentAt(new Date())
                    .body("")
                    .encoding(Charset.forName("UTF-8"))
                    .build();

            /* Параметры */
            Map<String, Object> paramMap = Maps.newHashMap();
            paramMap.put("fullName",subjectFullName);
            paramMap.put("site", Constants.HOME);
            paramMap.put("confirm", tokenLink);

            /* Конвертим его в Message */
            MimeMessage message = builder.setMessage(convert(email))
                    .setEmailContent(templateService.mergeTemplateIntoString(Constants.REGISTRATION_REQUEST, paramMap))
                    .build();

            /* отправляем */
            mailSender.send(message);
        } catch (MessagingException | IOException | TemplateException ex){
            log.error("Faild send registration email");
            result = false;
        }
        return result;
    }

    @Override
    public void sendRegistrationConfirm(String mailTo) throws IOException, MessagingException, TemplateException {
        EmailBuilder builder = EmailBuilder.getBuilder();
        /* получаем клиента */
        Subject subject = subjectService.getSubjectByEmail(mailTo);
        /* берем его полное имя */
        String subjectFullName = subject.getLastName() + " "+ subject.getFirstName();
        /* Получим объект сообщения */
        Email email = EmailImpl.builder()
                .from(new InternetAddress(senderAddress, senderName))
                .to(Lists.newArrayList(new InternetAddress(subject.getEmail(), subjectFullName)))
                .subject("Registration confirmation")
                .sentAt(new Date())
                .body("")
                .encoding(Charset.forName("UTF-8"))
                .build();

        /* Параметры */
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("fullName",subjectFullName);
        paramMap.put("site", Constants.HOME);

        /* Конвертим его в Message */
        MimeMessage message = builder.setMessage(convert(email))
                .setEmailContent(templateService.mergeTemplateIntoString(Constants.REGISTRATION_CONFIRM, paramMap))
                .build();

        /* отправляем */
        mailSender.send(message);
    }

    /**
     * Отправка токена для подтверждения изменения пароля. Используется, если не указан тел
     * @param mailTo
     * @param tokenLink
     * @throws IOException
     * @throws TemplateException
     * @throws MessagingException
     */
    @Override
    public void sendPassChangeConfirm(String mailTo, String tokenLink) throws IOException, TemplateException, MessagingException {
        EmailBuilder builder = EmailBuilder.getBuilder();
            /* получаем клиента */
        Subject subject = subjectService.getSubjectByEmail(mailTo);
            /* берем его полное имя */
        String subjectFullName = subject.getLastName() + " "+ subject.getFirstName();
            /* Получим объект сообщения */
        Email email = EmailImpl.builder()
                .from(new InternetAddress(senderAddress, senderName))
                .to(Lists.newArrayList(new InternetAddress(subject.getEmail(), subjectFullName)))
                .subject("Password change confirmation")
                .sentAt(new Date())
                .body("")
                .encoding(Charset.forName("UTF-8"))
                .build();

            /* Параметры */
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("fullName",subjectFullName);
        paramMap.put("confirm", tokenLink);

            /* Конвертим его в Message */
        MimeMessage message = builder.setMessage(convert(email))
                .setEmailContent(templateService.mergeTemplateIntoString(Constants.PASSWORD_CHANGE_REQUEST, paramMap))
                .build();

            /* отправляем */
        mailSender.send(message);
    }

    @Override
    public void sendNews(String mailTo) {
        log.info("Тут будет отправка новостей и акций");
    }

    @Override
    public List<Email> getInbox(){
        List<Email> emails = null;
        log.info("Тут будет получение всех входящих писем");
        return emails;
    }

    @Override
    public List<Email> getOutbox(){
        List<Email> emails = null;
        log.info("Тут будет получение всех отправленных писем");
        return emails;
    }

    public void sendOrderStatus(Order order) throws IOException, MessagingException, TemplateException {
        EmailBuilder builder = EmailBuilder.getBuilder();

        /* get all info */
        Map<String, Object> info = orderService.getOrderInfo(order.getId());
//        Map<String, OrderItem> orderItemMap = (Map<String, OrderItem>) info.get("orderItems");
        Map<String, Item> itemMap = (Map<String, Item>) info.get("items");
        Map<String, Content> contentMap = (Map<String, Content>) info.get("contents");
        List<Map<String, String>> list = Lists.newArrayList();

        /* перебираем товар */
        itemMap.keySet().forEach(itemId -> list.add(new ImmutableMap.Builder<String, String>()
						.put("imageName", contentMap.get(itemId).getId())
						.put("itemName", itemMap.get(itemId).getName())
						.put("itemCost", itemMap.get(itemId).getPrice()+ " руб")
//						.put("itemCount", orderItemMap.get(itemId).getCou() + " шт")
				.build()));


        /* build template data */
        final Map<String, Object> data = new ImmutableMap.Builder<String, Object>()
				/* основной текст письма */
                .put("order_number", order.getUid())
                .put("order_status", order.getStatus().getNotifyText())
                .put("isAuth", true)
                .put("cabinet_link", Constants.CABINET_LINK)
				/* информация о заказе */
                .put("orderDate", order.getDateAdd() == null ? new Date() : order.getDateAdd())
                .put("orderDelivery", order.getDelivery())
                .put("orderPayment", order.getPayment()+ " руб")
				/* данные о товаре */
                .put("item", list)
                .build();

        /* Создадим сообщение */
        Email email = EmailImpl.builder()
                .from(new InternetAddress(senderAddress, senderName))
                .to(Lists.newArrayList(new InternetAddress(order.getRecipientEmail(), order.getRecipientFname())))
                .subject("Информация о заказе, "+order.getRecipientFname())
                .sentAt(new Date())
                .body("")
                .encoding(Charset.forName("UTF-8"))
                .build();

        MimeMessage message = builder.setMessage(convert(email))
                .setInlinePictures(collectPics(Lists.newArrayList(contentMap.values())))
                .setEmailContent(templateService.mergeTemplateIntoString(Constants.ORDER_EMAIL, data))
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

    /**
     * Создадим обертки для изображений
     * @param contents
     * @return
     */
    private List<InlinePicture> collectPics(List<Content> contents) throws IOException {
        List<InlinePicture> pictures = Lists.newArrayList();
        pictures.addAll(contents.stream().map(content -> {
            InlinePicture inlinePicture = null;
            try {
                inlinePicture = InlinePictureImpl.builder()
                        .content(Base64BytesSerializer.deserialize(content.getContent()))
                        .imageType(ImageType.valueOf(content.getType().toUpperCase()))
                        .templateName(content.getId()).build();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return inlinePicture;
        }).collect(Collectors.toList()));
        return pictures;
    }

}
