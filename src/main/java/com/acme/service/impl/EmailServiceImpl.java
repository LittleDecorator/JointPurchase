package com.acme.service.impl;

import com.acme.constant.Constants;
import com.acme.email.Email;
import com.acme.email.ImageType;
import com.acme.email.InlinePicture;
import com.acme.email.impl.EmailAttachmentImpl;
import com.acme.email.impl.EmailImpl;
import com.acme.email.impl.InlinePictureImpl;
import com.acme.enums.GmailLabels;
import com.acme.exception.EmailConversionException;
import com.acme.exception.TemplateException;
import com.acme.handlers.Base64BytesSerializer;
import com.acme.model.*;
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
import com.google.api.services.gmail.model.Draft;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;
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

//    @PostConstruct
//    private void init() throws IOException {
//        /* получение всех сообщений (полная синхронизация) */
//        messages = helper.getMessages();
//        /* получение последнего ID в истории */
//        lastHistoryId = messages.get(0).getHistoryId();
//    }

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

            /* Параметры */
            Map<String, Object> paramMap = Maps.newHashMap();
            paramMap.put("fullName",subjectFullName);
            paramMap.put("site", HOME);
            paramMap.put("confirm", tokenLink);

            /* Конвертим его в Message */
            MimeMessage message = builder.setMessage(convert(email))
                    .setEmailContent(templateService.mergeTemplateIntoString(Constants.REGISTRATION_REQUEST, paramMap))
                    .build();

            /* отправляем */
            mailSender.send(message);
//            SimpleMessage sended = SimpleMessage.valueOf(helper.sendMessage(message));
//            /* добавим сообщение во исходящие */
//            messages.add(sended);
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
        mailSender.send(message);
//        SimpleMessage sended = SimpleMessage.valueOf(helper.sendMessage(message));
//        /* добавим сообщение во исходящие */
//        messages.add(sended);
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
            mailSender.send(message);
//            SimpleMessage sended = SimpleMessage.valueOf(helper.sendMessage(message));
//        /* добавим сообщение в общий список */
//            messages.add(sended);
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
        mailSender.send(message);
//        SimpleMessage sended = SimpleMessage.valueOf(helper.sendMessage(message));
//        /* добавим сообщение в общий список */
//        messages.add(sended);
//        TODO: добавить обновление ID истории
    }

    public Boolean sendOrderStatus(Order order){
        Boolean result = Boolean.TRUE;
        try {
            EmailBuilder builder = EmailBuilder.getBuilder();

        /* порлучаем всю информацию по заказу */
            Map<String, Object> info = orderService.getOrderInfo(order.getId());
            Map<String, OrderItem> orderItemMap = (Map<String, OrderItem>) info.get("orderItems");
            Map<String, Item> itemMap = (Map<String, Item>) info.get("items");
            Map<String, Content> contentMap = (Map<String, Content>) info.get("contents");

            List<Map<String, String>> list = Lists.newArrayList();
        /* перебираем товар */
            if(itemMap!=null && !itemMap.isEmpty()){
                itemMap.keySet().forEach(itemId -> list.add(new ImmutableMap.Builder<String, String>()
                        .put("imageName", contentMap.get(itemId).getId())
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
                    .put("cabinet_link", Constants.CABINET_LINK)
				/* информация о заказе */
                    .put("orderDate", order.getDateAdd() == null ? new Date() : order.getDateAdd())
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
                    .setInlinePictures(collectPics(Lists.newArrayList(contentMap.values())))
                    .setEmailContent(emailContent)
                    .build();

        /* отправим письмо */
            mailSender.send(message);
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

    /**
     * Получение нитий сообщений конкретных Label без фильтрации и со стандартным размером
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
}
