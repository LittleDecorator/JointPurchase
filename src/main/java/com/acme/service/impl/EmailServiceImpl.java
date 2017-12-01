package com.acme.service.impl;

import com.acme.constant.Constants;
import com.acme.email.Email;
import com.acme.email.ImageType;
import com.acme.email.InlinePicture;
import com.acme.email.impl.EmailAttachmentImpl;
import com.acme.email.impl.EmailImpl;
import com.acme.email.impl.InlinePictureImpl;
import com.acme.email.oauth.OAuth2Authenticator;
import com.acme.enums.GmailLabels;
import com.acme.exception.EmailConversionException;
import com.acme.exception.TemplateException;
import com.acme.handlers.Base64BytesSerializer;
import com.acme.model.Content;
import com.acme.model.Item;
import com.acme.model.Order;
import com.acme.model.OrderItem;
import com.acme.model.Subject;
import com.acme.model.gmail.SimpleDraft;
import com.acme.model.gmail.SimpleMessage;
import com.acme.model.gmail.SimpleThread;
import com.acme.repository.DeliveryRepository;
import com.acme.service.EmailService;
import com.acme.service.OrderService;
import com.acme.service.SubjectService;
import com.acme.service.TemplateService;
import com.acme.util.EmailBuilder;
import com.acme.util.GmailHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.model.Draft;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.io.ByteStreams;
import com.sun.mail.smtp.SMTPTransport;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.acme.email.oauth.OAuth2Authenticator.initialize;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Value("${app.name}")
    private String senderName;

    @Value("${spring.mail.username}")
    private String senderAddress;

    @Value("${app.home}")
    private String HOME;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private GmailHelper helper;

    private List<SimpleMessage> messages;
    private BigInteger lastHistoryId;

    /* время жизни ключа доступа к сервису Gmail */
    private long tokenExpires = -1L;
    /* ключ доступа к сервису Gmail */
    private String accessToken = "";

    @PostConstruct
    private void init() throws IOException {
        /* получение всех сообщений (полная синхронизация) */
//        messages = helper.getMessages();
        /* получение последнего ID в истории */
//        lastHistoryId = messages.get(0).getHistoryId();

        /* инициализация аутентификатора */
        initialize();
    }

    @Override
    public Boolean sendSimple(String mailTo, String subject, String content) throws IOException, TemplateException, MessagingException{
        Boolean result = Boolean.TRUE;
        try {
            EmailBuilder builder = EmailBuilder.getBuilder();
        /* Получим объект сообщения */
            Email email = EmailImpl.builder()
                    .from(new InternetAddress(senderAddress, senderName))
                    .to(Lists.newArrayList(new InternetAddress(mailTo)))
                    .subject(subject)
                    .sentAt(new Date())
                    .body("")
                    .encoding(Charset.forName("UTF-8"))
                    .build();

        /* Параметры */
            Map<String, Object> paramMap = Maps.newHashMap();
            paramMap.put("CONTENT", content);

        /* Конвертим его в Message */
            MimeMessage message = builder.setMessage(convert(email))
                    .setEmailContent(templateService.mergeTemplateIntoString(Constants.SIMPLE_EMAIL, paramMap))
                    .build();

        /* отправляем */
            //TODO: либо можо использовать JavaMailSender
            //getTransport().sendMessage(message, message.getAllRecipients());

        /* сперва добаляем auth токен и сессию, затем отправляем */
            prepareOAuthSender().send(message);
            //TODO: spring mail sender пока не используем. Нужен конкретный механизм отключения PLAIN LOGIN
            //mailSender.send(message);
            //TODO: если gmail научится работать с inlineImage, то полностью перейдем на его API
            //SimpleMessage sended = SimpleMessage.valueOf(helper.sendMessage(message));
            ///* добавим сообщение в общий список */
            //messages.add(sended);
            //TODO: добавить обновление ID истории
        } catch (IOException | MessagingException | TemplateException ex){
            result = Boolean.FALSE;
            log.error("Ошибка отправки письма", ex);
        }
        //TODO: добавить обновление ID истории
        return result;
    }

    @Override
    @Async
    public Boolean sendRegistrationToken(String mailTo, String tokenLink) {
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
                    .subject("Подтверждение регистрации")
                    .sentAt(new Date())
                    .body("")
                    .encoding(Charset.forName("UTF-8"))
                    .build();

            /* Параметры для шаблона письма*/
            Map<String, Object> paramMap = Maps.newHashMap();
            paramMap.put("fullName",subjectFullName);
            paramMap.put("site", HOME);
            paramMap.put("confirm", tokenLink);

            /* Конвертим его в Message */
            MimeMessage message = builder.setMessage(convert(email))
                    .setEmailContent(templateService.mergeTemplateIntoString(Constants.REGISTRATION_REQUEST, paramMap))
                    .build();

            /* отправляем */
            //TODO: либо можно использовать JavaMailSender
//            getTransport().sendMessage(message, message.getAllRecipients());

            /* сперва добаляем auth токен и сессию, затем отправляем */
            prepareOAuthSender().send(message);

            //TODO: если не используется OAuth, то можно сразу отправлять
            //mailSender.send(message);

            //TODO: если gmail научится работать с inlineImage, то полностью перейдем на его API
            //SimpleMessage sended = SimpleMessage.valueOf(helper.sendMessage(message));
            ///* добавим сообщение во исходящие */
            //messages.add(sended);
            //TODO: добавить обновление ID истории

        } catch (MessagingException | IOException | TemplateException ex){
            log.error("Не удалось отправить письмо для подтверждения регистрации");
            result = false;
        }
        return result;
    }

    @Override
    @Async
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
                .subject("Подтверждение регистрации")
                .sentAt(new Date())
                .body("")
                .encoding(Charset.forName("UTF-8"))
                .build();

        /* Параметры */
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("fullName",subjectFullName);
        paramMap.put("site", HOME);

        /* Конвертим его в Message */
        MimeMessage message = builder.setMessage(convert(email))
                .setEmailContent(templateService.mergeTemplateIntoString(Constants.REGISTRATION_CONFIRM, paramMap))
                .build();

        /* отправляем */
        //TODO: либо можо использовать JavaMailSender
        //getTransport().sendMessage(message, message.getAllRecipients());

        /* сперва добаляем auth токен и сессию, затем отправляем */
        prepareOAuthSender().send(message);

        //TODO: если не используется OAuth, то можно сразу отправлять
        //mailSender.send(message);
        //TODO: spring mail sender пока не используем. Нужен конкретный механизм отключения PLAIN LOGIN
        //mailSender.send(message);
        //TODO: если gmail научится работать с inlineImage, то полностью перейдем на его API
        //SimpleMessage sended = SimpleMessage.valueOf(helper.sendMessage(message));
        ///* добавим сообщение во исходящие */
        //messages.add(sended);
        //TODO: добавить обновление ID истории
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
    @Async
    public Boolean sendPassChangeToken(String mailTo, String tokenLink){
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
                    .subject("Подтверждение смены пароля")
                    .sentAt(new Date())
                    .body("")
                    .encoding(Charset.forName("UTF-8"))
                    .build();

            /* Параметры */
            Map<String, Object> paramMap = Maps.newHashMap();
            paramMap.put("fullName",subjectFullName);
            paramMap.put("site", HOME);
            paramMap.put("confirm", tokenLink);

            /* Конвертим его в Message */
            MimeMessage message = builder.setMessage(convert(email))
                    .setEmailContent(templateService.mergeTemplateIntoString(Constants.PASSWORD_CHANGE_REQUEST, paramMap))
                    .build();

            /* отправляем */
            //TODO: либо можо использовать JavaMailSender
            //getTransport().sendMessage(message, message.getAllRecipients());

            /* сперва добаляем auth токен и сессию, затем отправляем */
            prepareOAuthSender().send(message);
            //TODO: spring mail sender пока не используем. Нужен конкретный механизм отключения PLAIN LOGIN
            //mailSender.send(message);
            //TODO: если gmail научится работать с inlineImage, то полностью перейдем на его API
            //SimpleMessage sended = SimpleMessage.valueOf(helper.sendMessage(message));
            ///* добавим сообщение в общий список */
            //messages.add(sended);
            //TODO: добавить обновление ID истории
        } catch (MessagingException | IOException | TemplateException ex){
            log.error("Не удалось отправить письмо для подтверждения изменения пароля");
            result = false;
        }
        return result;
    }

    /**
     * Отправка токена для подтверждения изменения пароля. Используется, если не указан тел
     * @param mailTo
     * @throws IOException
     * @throws TemplateException
     * @throws MessagingException
     */
    @Override
    @Async
    public void sendPassChangeConfirm(String mailTo) throws IOException, TemplateException, MessagingException{

        EmailBuilder builder = EmailBuilder.getBuilder();
        /* получаем клиента */
        Subject subject = subjectService.getSubjectByEmail(mailTo);
        /* берем его полное имя */
        String subjectFullName = subject.getLastName() + " "+ subject.getFirstName();
        /* Получим объект сообщения */
        Email email = EmailImpl.builder()
                .from(new InternetAddress(senderAddress, senderName))
                .to(Lists.newArrayList(new InternetAddress(subject.getEmail(), subjectFullName)))
                .subject("Подтверждение смены пароля")
                .sentAt(new Date())
                .body("")
                .encoding(Charset.forName("UTF-8"))
                .build();

        /* Параметры */
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("fullName",subjectFullName);
        paramMap.put("site", HOME);

        /* Конвертим его в Message */
        MimeMessage message = builder.setMessage(convert(email))
                .setEmailContent(templateService.mergeTemplateIntoString(Constants.PASSWORD_CHANGE_CONFIRM, paramMap))
                .build();

        /* отправляем */
        //TODO: либо можо использовать JavaMailSender
        //getTransport().sendMessage(message, message.getAllRecipients());

        /* сперва добаляем auth токен и сессию, затем отправляем */
        prepareOAuthSender().send(message);
        //TODO: spring mail sender пока не используем. Нужен конкретный механизм отключения PLAIN LOGIN
        //mailSender.send(message);
        //TODO: если gmail научится работать с inlineImage, то полностью перейдем на его API
        //SimpleMessage sended = SimpleMessage.valueOf(helper.sendMessage(message));
        ///* добавим сообщение в общий список */
        //messages.add(sended);
        //TODO: добавить обновление ID истории
    }

    public Boolean sendOrderStatus(Order order){
        Boolean result = Boolean.TRUE;
        try {
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            EmailBuilder builder = EmailBuilder.getBuilder();

            /* получаем всю информацию по заказу */
            Map<String, Object> info = orderService.getOrderInfo(order.getId());
            Map<String, OrderItem> orderItemMap = (Map<String, OrderItem>) info.get("orderItems");
            Map<String, Item> itemMap = (Map<String, Item>) info.get("items");
            Map<String, Content> contentMap = (Map<String, Content>) info.get("contents");

            List<Map<String, String>> list = Lists.newArrayList();
            /* перебираем товар */
            if(itemMap!=null && !itemMap.isEmpty()){
                itemMap.keySet().forEach(itemId -> list.add(new ImmutableMap.Builder<String, String>()
                        .put("imageName", "https://grimmstory.ru/media/image/preview/" + contentMap.get(itemId).getId())
                        .put("itemName", itemMap.get(itemId).getName())
                        .put("itemCost", itemMap.get(itemId).getPrice()+ " руб")
                        .put("itemCount", orderItemMap.get(itemId).getCount() + " шт")
                        .build()));
            }

        /* сформируем шаблон сообщения */
            final Map<String, Object> data = new ImmutableMap.Builder<String, Object>()
    				/* основной текст письма */
                    .put("order_number", order.getUid())
                    .put("order_status", order.getStatus().getNotifyText())
                    .put("isAuth", order.getSubjectId() != null)
                    .put("cabinet_link", Constants.CABINET_LINK + order.getId())
				    /* информация о заказе */
                    .put("orderDate", DATE_FORMAT.format(order.getDateAdd() == null ? new Date() : order.getDateAdd()))
                    .put("orderDelivery", deliveryRepository.findOne(order.getDelivery()).getName())
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

            String emailContent = templateService.mergeTemplateIntoString(Constants.ORDER_EMAIL, data);

            MimeMessage message = builder.setMessage(convert(email))
                    //.setInlinePictures(collectPics(Lists.newArrayList(contentMap.values())))
                    .setEmailContent(emailContent)
                    .build();

            /* отправляем */
            //TODO: либо можо использовать JavaMailSender
            //getTransport().sendMessage(message, message.getAllRecipients());

            /* сперва добаляем auth токен и сессию, затем отправляем */
            prepareOAuthSender().send(message);
            //TODO: spring mail sender пока не используем. Нужен конкретный механизм отключения PLAIN LOGIN
            //mailSender.send(message);
        } catch (IOException | MessagingException | TemplateException ex){
            result = Boolean.FALSE;
            log.error("Ошибка отправки письма", ex);
        }
        //TODO: добавить обновление ID истории
        return result;
    }

    @Override
    public void sendNews(String mailTo) {
        //TODO: Доделать!!!!!
        log.info("Тут будет отправка новостей и акций");
    }

    @Override
    public List<SimpleThread> getInbox() throws IOException {
        return getMessages(GmailLabels.INBOX);
    }

    @Override
    public List<SimpleThread> getSent() throws IOException {
        /* получим сообщения */
        return getMessages(GmailLabels.SENT);
    }

    @Override
    public List<SimpleThread> getTrash() throws IOException {
        /* получим сообщения */
        return getMessages(GmailLabels.TRASH);
    }

    @Override
    public void removeMessage(String id) {
        SimpleMessage trashed = SimpleMessage.valueOf(helper.trashMessage(id));
        /* обновим метку сообщения */
        messages.stream().filter(m -> m.getId().contentEquals(trashed.getId())).forEach(m -> m.setLabels(trashed.getLabels()));
    }

    @Override
    public void restoreMessage(String id) {
        SimpleMessage untrashed = SimpleMessage.valueOf(helper.untrashMessage(id));
        /* обновим метку сообщения */
        messages.stream().filter(m -> m.getId().contentEquals(untrashed.getId())).forEach(m -> m.setLabels(untrashed.getLabels()));
    }

    @Override
    public void removeThread(String id) {
        SimpleThread trashThread = SimpleThread.valueOf(helper.trashThread(id));
        /* обновим метку всех сообщений в цепочке */
        Map<String, SimpleMessage> trashedMessages = trashThread.getMessages().stream().collect(Collectors.toMap(SimpleMessage::getId, Function.identity()));
        messages.stream().filter(m -> trashedMessages.keySet().contains(m.getId())).forEach(m -> m.setLabels(trashedMessages.get(m.getId()).getLabels()));
    }

    @Override
    public void restoreThread(String id) {
        SimpleThread untrashThread = SimpleThread.valueOf(helper.untrashThread(id));
        /* обновим метку всех сообщений в цепочке */
        Map<String, SimpleMessage> trashedMessages = untrashThread.getMessages().stream().collect(Collectors.toMap(SimpleMessage::getId, Function.identity()));
        messages.stream().filter(m -> trashedMessages.keySet().contains(m.getId())).forEach(m -> m.setLabels(trashedMessages.get(m.getId()).getLabels()));
    }

    public void sendWithoutAttach(SimpleMessage message) throws IOException, MessagingException {
        SimpleMessage sended = SimpleMessage.valueOf(helper.sendMessage(message.getTo(), message.getSubject(), message.getBody()));
        messages.add(sended);
    }

    @Override
    public void insertToInbox(SimpleMessage message) throws IOException, MessagingException {
        messages.add(SimpleMessage.valueOf(helper.insertMessage(message.getTo(), message.getSubject(), message.getBody())));
    }

    @Override
    public List<SimpleDraft> getDraft() throws IOException {
        return getDrafts();
    }

    @Override
    public void removeDraft(String id) throws IOException {
        helper.deleteDraft(id);
        /* удалим из списка */
        messages.removeIf(message -> message.getId().contentEquals(id));
    }

    @Override
    public void saveDraft(SimpleDraft draft) throws IOException, MessagingException {
        SimpleMessage message = draft.getMessage();
        Draft result;
        if(Strings.isNullOrEmpty(draft.getId())){
            result = helper.createDraft(message.getTo(), message.getSubject(), message.getBody());
            /* добавим в список */
            messages.add(SimpleMessage.valueOf(result.getMessage()));
        } else {
            result = helper.updateDraft(draft.getId(), message.getTo(), message.getSubject(), message.getBody());
            /* получим обновленное сообщение черновика */
            SimpleMessage updatedMessage = SimpleMessage.valueOf(result.getMessage());
            /* обновим в списке */
            messages.stream().filter(m -> m.getId().contentEquals(updatedMessage.getId())).forEach(m -> m.merge(updatedMessage));
        }
    }

    @Override
    public SimpleDraft getDraft(String id) throws IOException {
        return helper.getDraft(id);
    }

    @Override
    public void sendDraft(SimpleDraft draft) throws IOException, MessagingException {
        SimpleMessage message = draft.getMessage();
        helper.updateDraft(draft.getId(), message.getTo(), message.getSubject(), message.getBody());
        helper.sendDraft(draft.getId());
        //TODO: возможно нужно удалять черновик после отправки
    }

    /*-------------- PRIVATE METHODS --------------*/

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
    private MimeMessage convert(Email email) throws MessagingException {
        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,
               Optional.ofNullable(email.getEncoding()).orElse(Charset.forName("UTF-8")).displayName());

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

    /**
     * Получение нитей сообщений конкретных Label без фильтрации и со стандартным размером
     * @param label
     * @return
     * @throws IOException
     */
    private List<SimpleThread> getMessages(GmailLabels label) throws IOException {
        /* отфильтруем входящие */
        List<SimpleMessage> inbox = messages.stream().filter(message -> message.getLabels().contains(label.name())).collect(Collectors.toList());
        /* группируем по цепочкам */
        Multimap<String, SimpleMessage> messages = Multimaps.index(inbox, SimpleMessage::getThreadId);
        /* получим все свежие цепочки во входящих */
        List<SimpleThread> result = helper.getThreads(label.asSingleList());
        for(SimpleThread simpleThread : result){
            simpleThread.setMessages((List<SimpleMessage>) messages.get(simpleThread.getId()));
        }
        return result;
    }

    /**
     * Получение черновиков
     * @return
     * @throws IOException
     */
    private List<SimpleDraft> getDrafts() throws IOException {
        /* отфильтруем сообщения черновиков */
        Map<String, SimpleMessage> draftMap = messages.stream().filter(message -> message.getLabels().contains(GmailLabels.DRAFT.name())).collect(Collectors.toMap(SimpleMessage::getId, Function.identity()));
        /* получим все черновики */
        List<SimpleDraft> result = helper.getDrafts();
        for(SimpleDraft draft : result){
            draft.setMessage(draftMap.get(draft.getMessage().getId()));
        }
        return result;
    }

    /**
     * Обновление метки истории
     */
    private void refreshHistory(){
        //TODO: Добавить обновление ID последней записи в истории
    }

    /**
     * Получение сессии нашего ключа доступа
     * @return
     */
    private Session getSession(){
        refreshTokenApi();
//        refreshToken();
        return OAuth2Authenticator.getSmtpSession(accessToken, true);
    }

    /**
     * Получение корректного транспорта для обращения к SMTP GMail с XOAUTH
     * @return
     * @throws MessagingException
     */
    private SMTPTransport getTransport() throws MessagingException {
        return OAuth2Authenticator.connectToSmtp("smtp.gmail.com", 587, senderAddress, getSession());
    }

    /**
     * Получение свежего ключа доступа через API Google
     */
    private void refreshTokenApi(){
        if(System.currentTimeMillis() > tokenExpires) {
            try{
                HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
                String oauthClientId = "379305360127-98jeqeehb4k2f9jcd97atnp7levi5cbl.apps.googleusercontent.com";
                String oauthSecret = "YA9liGGrxDsTLsxYw-u-R9g5";
                String refreshToken = "1/u4U3ozvJuOPCRz23PDhfCYS4H0OvB06Jllmc28I-Jdc";

                TokenResponse response = new GoogleRefreshTokenRequest(HTTP_TRANSPORT, JSON_FACTORY, refreshToken, oauthClientId, oauthSecret).execute();
                accessToken = response.getAccessToken();
                tokenExpires = System.currentTimeMillis() + response.getExpiresInSeconds()*1000;
            } catch (Throwable ex){
                ex.printStackTrace();
                Logger.getLogger(GmailHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Получение свежего ключа доступа
     */
    private void refreshToken(){
        //TODO: TOKEN_URL можно перенести в .properties. Проверить, можно ли использовать GMAIL API для обновления token'а
        String oauthClientId = "379305360127-98jeqeehb4k2f9jcd97atnp7levi5cbl.apps.googleusercontent.com";
        String oauthSecret = "YA9liGGrxDsTLsxYw-u-R9g5";
        String refreshToken = "1/u4U3ozvJuOPCRz23PDhfCYS4H0OvB06Jllmc28I-Jdc";
        String TOKEN_URL = "https://www.googleapis.com/oauth2/v4/token";

        if(System.currentTimeMillis() > tokenExpires) {
            try {
                String request = "client_id=" + URLEncoder.encode(oauthClientId, "UTF-8")
                                 + "&client_secret=" + URLEncoder.encode(oauthSecret, "UTF-8")
                                 + "&refresh_token=" + URLEncoder.encode(refreshToken, "UTF-8")
                                 + "&grant_type=refresh_token";
                HttpURLConnection conn = (HttpURLConnection) new URL(TOKEN_URL).openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(request); // note: println causes error
                out.flush();
                out.close();
                conn.connect();
                try {
                    HashMap<String,Object> result;
                    result = new ObjectMapper().readValue(conn.getInputStream(), new TypeReference<HashMap<String,Object>>() {});
                    accessToken = (String) result.get("access_token");
                    tokenExpires = System.currentTimeMillis()+(((Number)result.get("expires_in")).intValue()*1000);
                } catch (IOException e) {
                    String line;
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    while((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                    System.out.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Добавление необходимых свойств отправителю
     */
    private JavaMailSenderImpl prepareOAuthSender(){
        JavaMailSenderImpl sender = (JavaMailSenderImpl) mailSender;
        //TODO: нужно если без .properties
//        sender.setUsername(senderAddress);
//        sender.setHost("smtp.gmail.com");
//        sender.setPort(587);
        sender.setSession(getSession());
        sender.setPassword(accessToken);
        return sender;
    }
}
