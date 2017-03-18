package com.acme.gmail;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by kobzev on 09.03.17.
 */
public class GmailServiceTest {

	public static Map getMessageDetails(Gmail service, String messageId) {
		Map<String, Object> messageDetails = new HashMap<String, Object>();
		try {
//            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("Gmail Quickstart").build();
			Message message = service.users().messages().get("me", messageId).setFormat("raw").execute();

			byte[] emailBytes = Base64.decodeBase64(message.getRaw());

			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);

			MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
			messageDetails.put("subject", email.getSubject());
			messageDetails.put("from", email.getSender() != null ? email.getSender().toString() : "None");
			messageDetails.put("time", email.getSentDate() != null ? email.getSentDate().toString() : "None");
			messageDetails.put("snippet", message.getSnippet());
			messageDetails.put("threadId", message.getThreadId());
			messageDetails.put("id", message.getId());
			messageDetails.put("body", getText(email));

		} catch (MessagingException ex) {
//            Logger.getLogger(GoogleAuthHelper.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
//            Logger.getLogger(GoogleAuthHelper.class.getName()).log(Level.SEVERE, null, ex);
		}
		return messageDetails;

	}

	/**
	 * Get specified Label.
	 *
	 * @param service Authorized Gmail API instance.
	 * @param userId User's email address. The special value "me"
	 * can be used to indicate the authenticated user.
	 * @param labelId ID of Label to get.
	 * @throws IOException
	 */
	public static void getLabel(Gmail service, String userId, String labelId) throws IOException {
		Label label = service.users().labels().get(userId, labelId).execute();

		System.out.println("Label " + label.getName() + " retrieved.");
		System.out.println(label.toPrettyString());
	}

	public static void getLabels(Gmail service, String userId) throws IOException {
		ListLabelsResponse listResponse = service.users().labels().list(userId).execute();
		List<Label> labels = listResponse.getLabels();
		if (labels.size() == 0) {
			System.out.println("No labels found.");
		} else {
			System.out.println("Labels:");
			for (Label label : labels) {
				System.out.printf("- %s\n", label.getName());
			}
		}
	}


	/**
	 * List all Messages of the user's mailbox matching the query.
	 *
	 * @param service Authorized Gmail API instance.
	 * @param userId User's email address. The special value "me"
	 * can be used to indicate the authenticated user.
	 * @param query String used to filter the Messages listed.
	 * @throws IOException
	 */
	public static List<Message> listMessagesMatchingQuery(Gmail service, String userId, String query) throws IOException {
		ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();

		List<Message> messages = new ArrayList<>();
		while (response.getMessages() != null) {
			messages.addAll(response.getMessages());
			if (response.getNextPageToken() != null) {
				String pageToken = response.getNextPageToken();
				response = service.users().messages().list(userId).setQ(query)
						.setPageToken(pageToken).execute();
			} else {
				break;
			}
		}

		for (Message message : messages) {
			System.out.println(message.toPrettyString());
		}

		return messages;
	}


	/**
	 * Get Message with given ID.
	 *
	 * @param service Authorized Gmail API instance.
	 * @param userId User's email address. The special value "me"
	 * can be used to indicate the authenticated user.
	 * @param messageId ID of Message to retrieve.
	 * @return Message Retrieved Message.
	 * @throws IOException
	 */
	public static Message getMessage(Gmail service, String userId, String messageId)
			throws IOException {
		Message message = service.users().messages().get(userId, messageId).execute();

		System.out.println("Message: " + message.toPrettyString());
		String raw = message.getPayload().getParts().get(0).getBody().getData();
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println(raw);
		System.out.println(new String(DatatypeConverter.parseBase64Binary(raw), Charset.forName("UTF-8")));
		return message;
	}

	public static String getContent(Message message) {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			getPlainTextFromMessageParts(message.getPayload().getParts(), stringBuilder);
			byte[] bodyBytes = Base64.decodeBase64(stringBuilder.toString());
			String text = new String(bodyBytes, "UTF-8");
			return text;
		} catch (UnsupportedEncodingException e) {
//            logger.error("UnsupportedEncoding: " + e.toString());
			return message.getSnippet();
		}
	}

	private static void getPlainTextFromMessageParts(List<MessagePart> messageParts, StringBuilder stringBuilder) {
		for (MessagePart messagePart : messageParts) {
			if (messagePart.getMimeType().equals("text/plain")) {
				stringBuilder.append(messagePart.getBody().getData());
			}

			if (messagePart.getParts() != null) {
				getPlainTextFromMessageParts(messagePart.getParts(), stringBuilder);
			}
		}
	}

	public Map getBareGmailMessageDetails(Gmail service, String messageId) {
		Map<String, Object> messageDetails = new HashMap<String, Object>();
		try {
//            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("Gmail Quickstart").build();
			Message message = service.users().messages().get("me", messageId).setFormat("full")
					.setFields("id,payload,sizeEstimate,snippet,threadId").execute();
			List<MessagePartHeader> headers = message.getPayload().getHeaders();
			for (MessagePartHeader header : headers) {
				if (header.getName().equals("From") || header.getName().equals("Date")
					|| header.getName().equals("Subject") || header.getName().equals("To")
					|| header.getName().equals("CC")) {
					messageDetails.put(header.getName().toLowerCase(), header.getValue());
				}
			}
			messageDetails.put("snippet", message.getSnippet());
			messageDetails.put("threadId", message.getThreadId());
			messageDetails.put("id", message.getId());
			messageDetails.put("body", message.getPayload().getBody().getData());

		} catch (IOException ex) {

		}
		return messageDetails;

	}

	public static Map getBareMessageDetails(Gmail service, String messageId) {
		Map<String, Object> messageDetails = new HashMap<String, Object>();
		try {
//            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("Gmail Quickstart").build();
			Message message = service.users().messages().get("me", messageId).setFormat("raw").execute();

			byte[] emailBytes = Base64.decodeBase64(message.getRaw());

			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);

			MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
			messageDetails.put("subject", email.getSubject());
			messageDetails.put("from", email.getSender() != null ? email.getSender().toString() : "None");
			messageDetails.put("time", email.getSentDate() != null ? email.getSentDate().toString() : "None");
			messageDetails.put("snippet", message.getSnippet());
			messageDetails.put("threadId", message.getThreadId());
			messageDetails.put("id", message.getId());
			messageDetails.put("body", getText(email));

		} catch (MessagingException ex) {
//            Logger.getLogger(GoogleAuthHelper.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
//            Logger.getLogger(GoogleAuthHelper.class.getName()).log(Level.SEVERE, null, ex);
		}
		return messageDetails;

	}

	/**
	 * Return the primary text content of the message.
	 */
	private static String getText(Part p) throws
										  MessagingException, IOException {
		if (p.isMimeType("text/*")) {
			String s = (String) p.getContent();
//            textIsHtml = p.isMimeType("text/html");
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


	//    /**
//     * List all Threads of the user's mailbox matching the query.
//     *
//     * @param service Authorized Gmail API instance.
//     * @param userId User's email address. The special value "me"
//     * can be used to indicate the authenticated user.
//     * @param query String used to filter the Threads listed.
//     * @throws IOException
//     */
//    public static void listThreadsMatchingQuery (Gmail service, String userId,
//                                                 String query) throws IOException {
//        ListThreadsResponse response = service.users().threads().list(userId).setQ(query).execute();
//        List<Thread> threads = new ArrayList<Thread>();
//        while(response.getThreads() != null) {
//            threads.addAll(response.getThreads());
//            if(response.getNextPageToken() != null) {
//                String pageToken = response.getNextPageToken();
//                response = service.users().threads().list(userId).setQ(query).setPageToken(pageToken).execute();
//            } else {
//                break;
//            }
//        }
//
//        for(Thread thread : threads) {
//            System.out.println(thread.toPrettyString());
//        }
//    }
//
//    /**
//     * List all Threads of the user's mailbox with labelIds applied.
//     *
//     * @param service Authorized Gmail API instance.
//     * @param userId User's email address. The special value "me"
//     * can be used to indicate the authenticated user.
//     * @param labelIds String used to filter the Threads listed.
//     * @throws IOException
//     */
//    public static void listThreadsWithLabels (Gmail service, String userId,
//                                              List<String> labelIds) throws IOException {
//        ListThreadsResponse response = service.users().threads().list(userId).setLabelIds(labelIds).execute();
//        List<Thread> threads = new ArrayList<Thread>();
//        while(response.getThreads() != null) {
//            threads.addAll(response.getThreads());
//            if(response.getNextPageToken() != null) {
//                String pageToken = response.getNextPageToken();
//                response = service.users().threads().list(userId).setLabelIds(labelIds)
//                        .setPageToken(pageToken).execute();
//            } else {
//                break;
//            }
//        }
//
//        for(Thread thread : threads) {
//            System.out.println(thread.toPrettyString());
//        }
//    }

	//    public static String getMessage(Gmail service, String messageId) {
//        //helper function to get message details in JSON format
//        return new JSONObject(getMessageDetails(service, messageId)).toJSONString();
//    }


	//    /** Authorizes the installed application to access user's protected data. */
//    private static Credential authorize2() throws Exception {
//        // load client secrets
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(GmailTest.class.getResourceAsStream("/client_secrets.json")));
//            // set up authorization code flow
//            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder( HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
//                    Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory)
//                    .build();
//            // authorize
//            return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
//        }

}
