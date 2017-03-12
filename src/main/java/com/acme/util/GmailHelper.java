package com.acme.util;

import com.acme.model.gmail.SimpleDraft;
import com.acme.model.gmail.SimpleMessage;
import com.acme.model.gmail.SimpleThread;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.Joiner;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.History;
import com.google.api.services.gmail.model.ListDraftsResponse;
import com.google.api.services.gmail.model.ListHistoryResponse;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.Thread;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import java.io.BufferedReader;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import com.sun.mail.imap.protocol.BASE64MailboxDecoder;
import com.sun.mail.imap.protocol.BASE64MailboxEncoder;
import org.apache.http.conn.util.InetAddressUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * A helper class for Google's Gmail API.
 *
 */
@Component
public final class GmailHelper {

    /** Application name. */
    private static final String APPLICATION_NAME = "GrimmStory";

    /**
     * Directory to store user credentials.
     */
    private static final File DATA_STORE_DIR = new File(System.getProperty("user.home"), ".credentials/gmail-java-grimmstory");
    /**
     * Global instance of the
     * {@link FileDataStoreFactory} The best
     * practice is to make it a single globally shared instance across your
     * application.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    /**
     * Please provide a value for the CLIENT_SECRET constant before proceeding,
     * set this up at https://code.google.com/apis/console/
     */
    private static final String CLIENT_SECRET = "/client_secret.json";

    private static final Collection<String> SCOPE = Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_COMPOSE, GmailScopes.GMAIL_INSERT, GmailScopes.GMAIL_MODIFY, GmailScopes.GMAIL_READONLY);
//    private static final Collection<String> SCOPE = GmailScopes.all();

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    private Credential credential;

    private Gmail service;

    private boolean textIsHtml = false;

    private String user;

    /* Init transport and store */
    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Constructor initializes the Google Authorization Code Flow with CLIENT
     * ID, SECRET, and SCOPE
     */
    @Autowired
    public GmailHelper(@Value("${spring.mail.username}") String userId) {
        try {
            user = userId;
            InputStream in = GmailHelper.class.getResourceAsStream(CLIENT_SECRET);
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPE)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
            credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(userId);

            service = getGmailService();
        } catch (IOException ex) {
            Logger.getLogger(GmailHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Build and return an authorized Gmail client service.
     * @return an authorized Gmail client service
     * @throws IOException
     */
    private Gmail getGmailService(){
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }

    /**
     * List the Labels in the user's mailbox.
     *
     * can be used to indicate the authenticated user.
     * @throws IOException
     */
    public List<Label> getLabels() throws IOException {
        ListLabelsResponse response = service.users().labels().list("me").execute();
        return response.getLabels();
    }

    /**
     * List all Threads of the user's mailbox matching the query.
     *
     * can be used to indicate the authenticated user.
     * @param query String used to filter the Threads listed.
     * @throws IOException
     */
    public List<SimpleThread> getThreads(List<String> labels, Long limit, String query) throws IOException {
        List<SimpleThread> result = Lists.newArrayList();

        Gmail.Users.Threads.List threadQuery = service.users().threads().list("me").setLabelIds(labels).setQ(query).setMaxResults(limit);
        ListThreadsResponse response = threadQuery.execute();
        List<Thread> threads = new ArrayList<>();
        while(response.getThreads() != null) {
            threads.addAll(response.getThreads());
            if(response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = threadQuery.setPageToken(pageToken).execute();
            } else {
                break;
            }
        }
        /* конвертим в наш тип */
        result.addAll(threads.stream().map(SimpleThread::valueOf).collect(Collectors.toList()));
        return result;
    }

    public List<SimpleDraft> getDrafts() throws IOException {
        List<SimpleDraft> result = Lists.newArrayList();
		Gmail.Users.Drafts.List draftQuery = service.users().drafts().list("me");
        ListDraftsResponse response = draftQuery.execute();
		List<Draft> drafts = new ArrayList<>();
		while(response.getDrafts() != null) {
			drafts.addAll(response.getDrafts());
			if(response.getNextPageToken() != null) {
				String pageToken = response.getNextPageToken();
				response = draftQuery.setPageToken(pageToken).execute();
			} else {
				break;
			}
		}
        /* конвертим в наш тип */
        result.addAll(drafts.stream().map(SimpleDraft::valueOf).collect(Collectors.toList()));
        return result;
    }

    /**
     * Получение одной ветки сообщений
     * @param threadId
     * @return
     * @throws IOException
     */
    public Thread getThread(String threadId) throws IOException {
        Thread thread = service.users().threads().get("me", threadId).execute();
        System.out.println("Thread id: " + thread.getId());
        System.out.println("No. of messages in this thread: " + thread.getMessages().size());
        return thread;
    }

    /**
     * Получение истории изменений начиная с ID
     * @param startHistoryId
     * @return
     * @throws IOException
     */
    public List<History> getHistory(BigInteger startHistoryId) throws IOException {
        List<History> histories = Lists.newArrayList();
        Gmail.Users.History.List historyQuery = service.users().history().list("me").setStartHistoryId(startHistoryId);
        ListHistoryResponse response = historyQuery.execute();

        while (response.getHistory() != null) {
            histories.addAll(response.getHistory());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = historyQuery.setPageToken(pageToken).execute();
            } else {
                break;
            }
        }
        return histories;
    }

    /**
     * Получение Сообщений
     * @param labels
     * @param limit
     * @param filter
     * @return
     * @throws IOException
     */
    public List<SimpleMessage> getMessages(List<String> labels, Long limit, String filter) throws IOException {
        List<SimpleMessage> result = Lists.newArrayList();

        List<Message> messages = getMessagesIds(labels, limit, filter);
        /* get Batched */
        BatchRequest request = service.batch();
        //callback function. (Can also define different callbacks for each request, as required)
        JsonBatchCallback<Message> bc = new JsonBatchCallback<Message>() {

            @Override
            public void onSuccess(Message message, HttpHeaders responseHeaders) throws IOException {
                System.out.println("result from batch for "+ message.getId());
                result.add(SimpleMessage.valueOf(message, false));
            }

            @Override
            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
                System.out.println(e.toPrettyString());
            }
        };

        // queuing requests on the batch requests
        for (Message message : messages) {
            Gmail.Users.Messages.Get batch = service.users().messages().get("robot.grimmstory@gmail.com", message.getId());
            System.out.println("queue in batch for "+ message.getId());
            batch.queue(request, bc);
        }

        request.execute();
        return result;
    }

    /**
     * Получение ID сообщений
     * @param labels
     * @return
     * @throws IOException
     */
    private List<Message> getMessagesIds(List<String> labels, Long limit, String filter) throws IOException {
        ListMessagesResponse response;

        Gmail.Users.Messages.List list = service.users().messages().list("me").setLabelIds(labels).setQ(filter).setMaxResults(limit);
        response = list.execute();

        /* получение ID сообщений по Labels*/
        List<Message> messages = Lists.newArrayList();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = list.setPageToken(pageToken).execute();
            } else {
                break;
            }
        }

        return messages;
    }



    /**
     * Перемещение цепочки сообщений в корзину
     * @param threadId
     * @return
     */
    public String trashThread(String threadId){
        try {
            service.users().threads().trash("me", threadId).execute();
            return "success";
        } catch (IOException ex) {
            Logger.getLogger(GmailHelper.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }
    }

    /**
     *
     * @param threadId
     * @return
     */
    public String untrashThread(String threadId){
        try {
            service.users().threads().untrash("me", threadId).execute();
            return "success";
        } catch (IOException ex) {
            Logger.getLogger(GmailHelper.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }
    }

    /**
     * Перемещение сообщения в корзину
     * @param messageId
     * @return
     */
    public String trashMessage(String messageId) {
        try {
            service.users().messages().trash("me", messageId).execute();
            return "success";
        } catch (IOException ex) {
            Logger.getLogger(GmailHelper.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }
    }

    /**
     * Перемещение сообщения в корзину
     * @param messageId
     * @return
     */
    public String untrashMessage(String messageId) {
        try {
            service.users().messages().untrash("me", messageId).execute();
            return "success";
        } catch (IOException ex) {
            Logger.getLogger(GmailHelper.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }
    }

    /**
     * Получение сообщения по ID
     * @param messageId
     * @return
     */
    public SimpleMessage getMessage(String messageId) throws IOException {
        //helper function to get message details in JSON format
        Message message = service.users().messages().get("me", messageId).execute();
        return SimpleMessage.valueOf(message, false);
    }


    /**
     * Получение сообщения по ID
     * @param draftId
     * @return
     */
    public SimpleDraft getDraft(String draftId) throws IOException {
        //helper function to get message details in JSON format
        Draft draft = service.users().drafts().get("me", draftId).execute();
        return SimpleDraft.valueOf(draft);
    }

    /**
     *
     * @param to
     * @param subject
     * @param body
     * @return
     * @throws IOException
     * @throws MessagingException
     */
    public String createDraft(String to, String subject, String body) throws IOException, MessagingException {
        MimeMessage email = createMimeMessage(to, user, subject, body);
        Message message = createMessageWithEmail(email);
        Draft draft = new Draft();
        draft.setMessage(message);
        draft = service.users().drafts().create("me", draft).execute();
        System.out.println("draft id: " + draft.getId());
        System.out.println(draft.toPrettyString());
        if (draft.getId() != null) {
            return "success";
        } else {
            return "fail";
        }
    }

    /**
     * Delete draft email.
     *
     * can be used to indicate the authenticated user.
     * @param draftId ID of Draft to deleted.
     * @throws IOException
     */
    public void deleteDraft(String draftId) throws IOException{
        service.users().drafts().delete("me", draftId).execute();
        System.out.println("Draft with id: " + draftId + " deleted successfully.");
    }


    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * indicate the authenticated user.
     * @throws MessagingException
     * @throws IOException
     */
    public String sendDraft(String draftId) throws MessagingException, IOException {
        Draft draft = service.users().drafts().get("me", draftId).execute();
        Message message = service.users().drafts().send("me", draft).execute();
        System.out.println("Draft with ID: " + draftId + " sent successfully.");
        System.out.println("Draft sent as Message with ID: " + message.getId());
        if (message.getId() != null) {
            return "success";
        } else {
            return "fail";
        }
    }

    /**
     *
     * @param to
     * @param subject
     * @param body
     * @return
     * @throws IOException
     * @throws MessagingException
     */
    public String updateDraft(String draftId, String to, String subject, String body) throws IOException, MessagingException {
        MimeMessage email = createMimeMessage(to, user, subject, body);
        Message message = createMessageWithEmail(email);
        Draft draft = new Draft();
        draft.setMessage(message);
        draft = service.users().drafts().update("me", draftId, draft).execute();
        System.out.println("draft id: " + draft.getId());
        System.out.println(draft.toPrettyString());
        if (draft.getId() != null) {
            return "success";
        } else {
            return "fail";
        }
    }

    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * indicate the authenticated user.
     * @throws MessagingException
     * @throws IOException
     */
    public String sendMessage(String to, String subject, String body) throws MessagingException, IOException {

        MimeMessage email = createMimeMessage(to, user, subject, body);
        Message message = createMessageWithEmail(email);
        message = service.users().messages().send("me", message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        if (message.getId() != null) {
            return "success";
        } else {
            return "fail";
        }
    }

    public String insertMessage(String to, String subject, String body) throws MessagingException, IOException {

        MimeMessage email = createMimeMessage(to, user, subject, body);
        Message message = createMessageWithEmail(email);
        message.setLabelIds(Collections.singletonList("INBOX"));
        message = service.users().messages().insert("me", message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        if (message.getId() != null) {
            return "success";
        } else {
            return "fail";
        }
    }

    /**
     * Create a Message from an email
     *
     * @param email Email to be set to raw of message
     * @return Message containing base64 encoded email.
     * @throws IOException
     * @throws MessagingException
     */
    private Message createMessageWithEmail(MimeMessage email) throws MessagingException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        email.writeTo(baos);
        String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    private MimeMessage createMimeMessage(String to, String from, String subject, String body) {
        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage message = new MimeMessage(session);
            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(javax.mail.Message.RecipientType.TO,
                    new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(subject);

            // Send the actual HTML message, as big as you like
            message.setContent(body, "text/html");
            return message;
        } catch (MessagingException ex) {
            Logger.getLogger(GmailHelper.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Return the primary text content of the message.
     */
    private String getText(Part p) throws
                                   MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String) p.getContent();
            textIsHtml = p.isMimeType("text/html");
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null) {
                        text = getText(bp);
                    }
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null) {
                        return s;
                    }
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null) {
                    return s;
                }
            }
        }

        return null;
    }

    //    private String getBody(HttpServletRequest request) throws IOException {
//
//        String body = null;
//        StringBuilder stringBuilder = new StringBuilder();
//        BufferedReader bufferedReader = null;
//
//        try {
//            InputStream inputStream = request.getInputStream();
//            if (inputStream != null) {
//                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                char[] charBuffer = new char[128];
//                int bytesRead = -1;
//                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
//                    stringBuilder.append(charBuffer, 0, bytesRead);
//                }
//            } else {
//                stringBuilder.append("");
//            }
//        } catch (IOException ex) {
//            throw ex;
//        } finally {
//            if (bufferedReader != null) {
//                try {
//                    bufferedReader.close();
//                } catch (IOException ex) {
//                    throw ex;
//                }
//            }
//        }
//
//        body = stringBuilder.toString();
//        return body;
//    }

    //    /**
//     * Выборка нужных полей из RAW сообщения
//     * @param message
//     * @return
//     */
//    private Map simplifyRawMessage(Message message){
//        Map<String, Object> messageDetails = new HashMap<>();
//        try {
//            byte[] emailBytes = Base64.decodeBase64(message.getRaw());
//
//            Properties props = new Properties();
//            Session session = Session.getDefaultInstance(props, null);
//
//            MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
//            messageDetails.put("subject", email.getSubject());
//
//            List<String> senders = Lists.newArrayList();
//            for(Address address : email.getFrom()){
//                InternetAddress internetAddress = new InternetAddress(address.toString());
//                String sender = internetAddress.getPersonal() + " <"+internetAddress.getAddress()+">";
//                senders.add(sender);
//            }
//            messageDetails.put("from",  Joiner.on(';').join(senders));
//            messageDetails.put("time", email.getSentDate() != null ? email.getSentDate().toString() : "None");
//            messageDetails.put("snippet", message.getSnippet());
//            messageDetails.put("threadId", message.getThreadId());
//            messageDetails.put("id", message.getId());
//            messageDetails.put("body", getText(email));
//
//        } catch (MessagingException ex) {
//            Logger.getLogger(GmailHelper.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(GmailHelper.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return messageDetails;
//    }



//    public String setMessageLabel(String messageId, String labelName) {
//        try {
//            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("Gmail Quickstart").build();
//            //For now assuming that when a label is set, we make it read also.
//
//            List<String> labelsToRemove = new ArrayList();
//            labelsToRemove.add("UNREAD");
//            List<String> labelsToAdd = new ArrayList();
//            if (labelName != null) {
//                //this can be optimized by get all label names once they login
//                ListLabelsResponse response = service.users().labels().list("me").execute();
//                List<Label> labels = response.getLabels();
//                boolean labelExists = false;
//                for (Label label : labels) {
//                    //System.out.println(label.toPrettyString());
//                    if (label.getName().equalsIgnoreCase(labelName)) {
//                        labelsToAdd.add(label.getId());
//                        System.out.println("Adding label:" + labelName);
//                        labelExists = true;
//                        break;
//                    }
//                }
//
//                if (!labelExists) {
//                    Label label = new Label().setName(labelName)
//                            .setLabelListVisibility("labelShow")
//                            .setMessageListVisibility("show");
//                    label = service.users().labels().create("me", label).execute();
//                    labelsToAdd.add(label.getId());
//                }
//            }
//
//            ModifyMessageRequest mods;
//            mods = new ModifyMessageRequest().setRemoveLabelIds(labelsToRemove)
//                    .setAddLabelIds(labelsToAdd);
//            Message message = service.users().messages().modify("me", messageId, mods).execute();
//            return "success";
//        } catch (IOException ioe) {
//            System.out.println("ioerror");
//            //ioe.printStackTrace();
//            return "error";
//        }
//    }

}