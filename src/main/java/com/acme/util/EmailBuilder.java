package com.acme.util;

import com.acme.email.InlinePicture;
import lombok.extern.slf4j.Slf4j;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
public class EmailBuilder {

    private MimeMessage message;
    private List<InlinePicture> inlinePictures;
    private String emailContent;

    public EmailBuilder setMessage(MimeMessage message) throws MessagingException {
        this.message = message;
        return this;
    }

    public EmailBuilder setInlinePictures(List<InlinePicture> inlinePictures) throws MessagingException {
        this.inlinePictures = inlinePictures;
        return this;
    }

    public EmailBuilder setEmailContent(String emailContent) throws MessagingException {
        this.emailContent = emailContent;
        return this;
    }

    /**
     * Собираем сообщение для отправки
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public MimeMessage build() throws MessagingException, IOException {
        //получим mime из email
        Multipart multipart = new MimeMultipart("related");
        /* делаем inline если изображения присутствуют */
        if(inlinePictures != null && !inlinePictures.isEmpty()){
            addImages(multipart);
        }
        final MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(emailContent, "utf-8", "html");
        multipart.addBodyPart(textPart);
        message.setContent(multipart);
        return message;
    }

    private EmailBuilder() {}

    public static EmailBuilder getBuilder(){
        log.info("message created");
        return new EmailBuilder();
    }

    /**
     * Делаем Inlining изображений в тело письма
     * @param multipart
     * @throws IOException
     * @throws MessagingException
     */
    private void addImages(Multipart multipart) throws IOException, MessagingException{
        for (final InlinePicture inlinePicture : inlinePictures) {
            final String cid = UUID.randomUUID().toString();

            //Set the cid in the template
            emailContent = emailContent.replace(inlinePicture.getTemplateName(), "cid:" + cid);

            //Set the image part
            final MimeBodyPart imagePart = new MimeBodyPart();
//			imagePart.attachFile(inlinePicture.getFile());
            ByteArrayDataSource dataSource = new ByteArrayDataSource( inlinePicture.getContent(), "image/jpg" );
            imagePart.setDataHandler(new DataHandler(dataSource));
            imagePart.setContentID('<' + cid + '>');
            imagePart.setDisposition(MimeBodyPart.INLINE);
            imagePart.setHeader("Content-Type", inlinePicture.getImageType().getContentType());
            multipart.addBodyPart(imagePart);
        }
    }
}
