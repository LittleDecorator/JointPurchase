package com.acme.gmail;

import com.acme.util.GmailHelper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import com.google.api.services.gmail.model.Thread;
import org.json.simple.JSONObject;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by nikolay on 04.03.17.
 */
public class GmailTest {

//    /** Application name. */
//    private static final String APPLICATION_NAME = "GrimmStory";
//
//    /** Directory to store user credentials for this application. */
//    private static final java.io.File DATA_STORE_DIR = new java.io.File( System.getProperty("user.home"), ".credentials/gmail-java-grimmstory");
//
//    /** Global instance of the {@link FileDataStoreFactory}. */
//    private static FileDataStoreFactory DATA_STORE_FACTORY;
//
//    /** Global instance of the JSON factory. */
//    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//
//    /** Global instance of the HTTP transport. */
//    private static HttpTransport HTTP_TRANSPORT;
//
//    /** Global instance of the scopes required by this quickstart.
//     *
//     * If modifying these scopes, delete your previously saved credentials
//     * at ~/.credentials/gmail-java-quickstart
//     */
//    private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY);
//
//    static {
//        try {
//            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
//        } catch (Throwable t) {
//            t.printStackTrace();
//            System.exit(1);
//        }
//    }
//
//    /**
//     * Creates an authorized Credential object.
//     * @return an authorized Credential object.
//     * @throws IOException
//     */
//    public static Credential authorize() throws IOException, GeneralSecurityException {
//        // Load client secrets.
//        InputStream in = GmailTest.class.getResourceAsStream("/client_secret.json");
//
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        // Build flow and trigger user authorization request.
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                        .setDataStoreFactory(DATA_STORE_FACTORY)
//                        .setAccessType("offline")
//                        .build();
////        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
//        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("robot.grimmstory@gmail.com");
//        System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
//        return credential;
//    }
//
//
//
//    /**
//     * Build and return an authorized Gmail client service.
//     * @return an authorized Gmail client service
//     * @throws IOException
//     */
//    public static Gmail getGmailService() throws IOException, GeneralSecurityException {
//        Credential credential = authorize();
//        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
//    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {

        GmailHelper helper = new GmailHelper("robot.grimmstory@gmail.com");
//        System.out.println("/* Получение используемых меток (labels) */");
//        /* Получение используемых меток (labels) */
//        System.out.println(helper.getLabels().stream().map(Label::getName).collect(Collectors.toList()));
//        System.out.println("\n\n");
//
//        System.out.println("/* Получение Thread'ов входящих и не прочитанных сообщений без фильтров */");
//        /* Получение Thread'ов входящих и не прочитанных сообщений без фильтров */
//        for(Thread thread : helper.getThreads(Arrays.asList("INBOX"),"is:unread")){
//            System.out.println(thread.toPrettyString());
//        }
//        System.out.println("\n\n");
//
//        System.out.println("/* Получение Thread'ов входящих и прочитанных сообщений без фильтров */");
//        /* Получение Thread'ов входящих и прочитанных сообщений без фильтров */
//        for(Thread thread : helper.getThreads(Arrays.asList("INBOX"),"is:read")){
//            System.out.println(thread.toPrettyString());
//        }
//        System.out.println("\n\n");
//
//        System.out.println("/* Получение Thread'ов отправленных сообщений без фильтров */");
//        /* Получение Thread'ов отправленных сообщений без фильтров */
//        for(Thread thread : helper.getThreads(Collections.singletonList("SENT"),"")){
//            System.out.println(thread.toPrettyString());
//        }
//        System.out.println("\n\n");
//
//        System.out.println("/* Получение Thread'а по ID - 157a80dc5c0f08bd */");
//        /* Получение Thread'а по ID */
//        System.out.println(helper.getThread("157a80dc5c0f08bd").toPrettyString());
//        System.out.println("\n\n");

//        System.out.println("/* Получение всех сообщений */");
//        /* Получение всех сообщений (полная форма) */
//        System.out.println(helper.getMessages(null, -1, null, false, false, false));
//        System.out.println("\n\n");

//        System.out.println("/* Получение входящих, не прочитанных сообщений */");
//        /* Получение всех сообщений (полная форма) */
//        System.out.println(helper.getMessages(Arrays.asList("INBOX"), -1, "is:unread", false, false, false));
//        System.out.println("\n\n");

//        System.out.println("/* Получение сообщения по ID */");
//        /* Получение сообщения по ID */
//        System.out.println(helper.getMessage("15ab360b3f297274", false, false));
//        System.out.println("\n\n");


//        System.out.println("/* Получение истории (частичная синхронизация) после ID - 3074 */");
////        /* Получение истории */
//        for(History history : helper.getHistory(BigInteger.valueOf(3074))){
//            System.out.println(history.toPrettyString());
//        }
//        System.out.println("\n\n");
    }

}
