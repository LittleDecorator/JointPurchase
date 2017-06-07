package com.acme.gmail;

import com.acme.email.Email;
import com.acme.email.impl.EmailAttachmentImpl;
import com.acme.email.impl.EmailImpl;
import com.acme.email.oauth.OAuth2Authenticator;
import com.acme.exception.EmailConversionException;
import com.acme.util.EmailBuilder;
import com.acme.util.GmailHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.gmail.GmailScopes;
import com.google.common.collect.Lists;
import com.sun.mail.smtp.SMTPTransport;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.acme.email.oauth.OAuth2Authenticator.initialize;
import static com.google.common.base.Optional.fromNullable;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

/**
 * Created by kobzev on 06.06.17.
 */
public class TokenTest {



	private final String CLIENT_ID = "379305360127-98jeqeehb4k2f9jcd97atnp7levi5cbl.apps.googleusercontent.com";
	private final String SECRET = "YA9liGGrxDsTLsxYw-u-R9g5";
	private final Collection<String> SCOPE = Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_COMPOSE, GmailScopes.GMAIL_INSERT, GmailScopes.GMAIL_MODIFY, GmailScopes.GMAIL_READONLY);

	private final File DATA_STORE_DIR = new File(System.getProperty("user.home"), ".credentials/gmail-java-grimmstory");
	private final String CLIENT_SECRET = "/client_secret.json";
	private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private FileDataStoreFactory DATA_STORE_FACTORY;
	private HttpTransport HTTP_TRANSPORT;

	private static long tokenExpires = 1458168133864L;

	private String TOKEN_URL = "https://www.googleapis.com/oauth2/v4/token";
	private JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	private String oauthClientId = "379305360127-98jeqeehb4k2f9jcd97atnp7levi5cbl.apps.googleusercontent.com";
	private String oauthSecret = "YA9liGGrxDsTLsxYw-u-R9g5";
	private String refreshToken = "1/u4U3ozvJuOPCRz23PDhfCYS4H0OvB06Jllmc28I-Jdc";
	private String accessToken = "ya29.GlxiBIDNywunCCSDJXJ5KeQRNHVR7uAGKDbCBv8N-0x-_81VuMvwCjLx0507mkXNPXMXqdA07JKLDJFIViexB6W1kORKyKYG2Ahm63fkFmRpD9lj-MrZO-zaj_abVg";

	public static void main(String[] args) throws Exception {
//		sendMailAdvance("kobzeff.inc@mail.ru", "test", "");
		TokenTest test = new TokenTest();
//		test.checkToken();
//		test.refreshTokenApi();
//		test.refreshToken();
		test.testSendToken();
	}


//	void requestAccessToken() throws IOException {
//		try {
//			GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(),
//							CLIENT_ID, SECRET, "4/cQCHoLRR1qnc9ENpnx77RyzHvtiApOqW8oEyVio9a3w", "https://oauth2-login-demo.appspot.com/code")
//							.execute();
//			System.out.println("Access token: " + response.getAccessToken());
//		} catch (TokenResponseException e) {
//			if (e.getDetails() != null) {
//				System.err.println("Error: " + e.getDetails().getError());
//				if (e.getDetails().getErrorDescription() != null) {
//					System.err.println(e.getDetails().getErrorDescription());
//				}
//				if (e.getDetails().getErrorUri() != null) {
//					System.err.println(e.getDetails().getErrorUri());
//				}
//			} else {
//				System.err.println(e.getMessage());
//			}
//		}
//	}
	private void refreshTokenApi(){
		try{
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
			TokenResponse response = new GoogleRefreshTokenRequest(HTTP_TRANSPORT, JSON_FACTORY, refreshToken, CLIENT_ID, SECRET).execute();
			accessToken = response.getAccessToken();
			System.out.println("Access token: " + accessToken);
		} catch (Throwable ex){
			ex.printStackTrace();
			Logger.getLogger(GmailHelper.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	void checkToken(){
		try{
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
			InputStream in = GmailHelper.class.getResourceAsStream(CLIENT_SECRET);
			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
					.Builder(HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, SECRET, SCOPE)
					.setAccessType("offline")
					.setCredentialDataStore(new MemoryDataStoreFactory().getDataStore("tokens"))
					.build();
			String userId = "robot.grimmstory@gmail.com";
			Credential credential = flow.loadCredential(userId);
			System.out.println("CREDENTIAL: "+credential);
			if (credential == null) {
				GoogleAuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl();
				authorizationUrl.setRedirectUri(GoogleOAuthConstants.OOB_REDIRECT_URI);
				System.out.println(authorizationUrl);
				Logger.getLogger(TokenTest.class.getName()).log(Level.SEVERE, "Please, authorize application. Visit {}", authorizationUrl);
				Scanner s = new Scanner(System.in);
				String code = s.nextLine();
				refreshToken = code;
				System.out.println("Refresh token: " + refreshToken);
				GoogleAuthorizationCodeTokenRequest tokenRequest = flow.newTokenRequest(code);
				tokenRequest.setRedirectUri(GoogleOAuthConstants.OOB_REDIRECT_URI);
				GoogleTokenResponse tokenResponse = tokenRequest.execute();
				accessToken = tokenResponse.getAccessToken();
				System.out.println("Access token: " + accessToken);
				credential = flow.createAndStoreCredential(tokenResponse, userId);
				System.out.println("Credential: " + credential);
			}
		} catch (Throwable ex){
			ex.printStackTrace();
            Logger.getLogger(GmailHelper.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	void testSendToken(){
		try{
			String userName = "robot.grimmstory@gmail.com";
//			String oauthToken = "ya29.GlxiBA0lz8SdXVpu7EA78BpU-h1xmIJVxMoUUNptgHcZ9M8Gm-fWO1b6EEBQf6_-1Yes0gaXSVNO9nadSOLlzbeccAgu3vzBNXBth34y8PPXy5gjE-TOBLTZYUFwZA";
			initialize();

			Session session = OAuth2Authenticator.getSmtpSession(accessToken, true);
			SMTPTransport transport = OAuth2Authenticator.connectToSmtp("smtp.gmail.com", 587, userName, session);
			EmailBuilder builder = EmailBuilder.getBuilder();

				Email email = EmailImpl.builder()
						.from(new InternetAddress("robot.grimmstory@gmail.com", "Grimm"))
						.to(Lists.newArrayList(new InternetAddress("kobzeff.inc@mail.ru", "BLA")))
						.subject("TEST")
						.sentAt(new Date())
						.body("VIA MAIL SENDER")
						.encoding(Charset.forName("UTF-8"))
						.build();

            /* Конвертим его в Message */
				MimeMessage message = builder.setMessage(convert(email))
						.setEmailContent("ALOHA")
						.build();


			mailSender.setUsername(userName);
			mailSender.setHost("smtp.gmail.com");
			mailSender.setPort(587);
			mailSender.setSession(session);
			mailSender.setPassword(accessToken);
			mailSender.send(message);

//			transport.sendMessage(message, message.getAllRecipients());
		} catch (Exception ex){
			ex.printStackTrace(System.out);
		}
	}

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
						System.out.println("Error while converting Email to MimeMessage");
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
			System.out.println("Error while converting Email to MimeMessage");
			throw new EmailConversionException(e);
		}

		return mimeMessage;
	}

	private void refreshToken(){
		try {
			String request = "client_id="+URLEncoder.encode(oauthClientId, "UTF-8")
							 +"&client_secret="+URLEncoder.encode(oauthSecret, "UTF-8")
							 +"&refresh_token="+URLEncoder.encode(refreshToken, "UTF-8")
							 +"&grant_type=refresh_token";
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
				System.out.println("NEW TOKEN: "+accessToken);
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
