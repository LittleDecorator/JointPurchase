//import com.acme.service.impl.EmailServiceImpl;
//
//import javax.mail.*;
//import javax.mail.internet.MimeMessage;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Properties;
//
//public class InboxMailTest {
//
//    public static void main(String[] args){
//
//        EmailServiceImpl emailService = new EmailServiceImpl();
//        Properties props = new Properties();
//
//        props.put("mail.smtp.host", "grimmstory.ru");
////        props.put("mail.debug", "true");
//        props.put("mail.smtp.port", 587);
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.ssl.trust", "grimmstory.ru");
//        props.put("mail.store.protocol", "imaps");
//
////        Properties props = new Properties();
////        props.setProperty("mail.store.protocol", "imaps");
//        try {
////            Session session = Session.getInstance(props, null);
//            Session session = Session.getInstance(props,
//                    new javax.mail.Authenticator() {
//                        protected PasswordAuthentication getPasswordAuthentication() {
//                            return new PasswordAuthentication("bobby", "12345678");
//                        }
//                    });
//
//            Store store = session.getStore("pop3");
//            store.connect("80.78.247.250", null, null);
//
//
//            /*Folder[] folders = store.getDefaultFolder().list("*");
//            for (Folder folder : folders) {
//                if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
//                    System.out.println(folder.getFullName() + ": " + folder.getMessageCount());
//                }
//            }*/
//
//
//
//          /*  Folder def = store.getDefaultFolder();
//            System.out.println(def.getType());
////            System.out.println(def.getFullName());
//            Folder[] folders = store.getDefaultFolder().list("%");
//            for (Folder folder : folders) {
////                if ((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {
//                    System.out.println(folder.getFullName());
////                }
//            }*/
//
//            Folder inbox = store.getFolder("INBOX");
//
//            inbox.open(Folder.READ_WRITE);
//            Message[] msgs = inbox.getMessages();
//            if (msgs.length == 0 && show) {
//                System.out.println("No messages in inbox");
//            }
//            for (int i = 0; i < msgs.length; i++) {
//                MimeMessage msg = (MimeMessage) msgs[i];
//                if (show) {
//                    System.out.println("    From: " + msg.getFrom()[0]);
//                    System.out.println(" Subject: " + msg.getSubject());
//                    System.out.println(" Content: " + msg.getContent());
//                }
//                if (clear) {
//                    msg.setFlag(Flags.Flag.DELETED, true);
//                }
//            }
//            inbox.close(true);
//            store.close();
//            System.out.println();
//
//
//
////            Folder inbox = store.getFolder("[INBOX]");
////            Folder inbox = store.getFolder("[INBOX]");
////            System.out.println(inbox.getType());
////            inbox.open(Folder.HOLDS_FOLDERS);
////            Folder inbox = store.getFolder("INBOX/Sent");
////            System.out.println(store.getFolder("[INBOX]/Sent"));
//
////            System.out.println(sent.exists());
////            System.out.println(inbox.exists());
////            System.out.println(inbox.isOpen());
////            Folder inbox = store.getFolder("[INBOX]");
////            inbox.open(Folder.READ_ONLY);
////            Folder[] f = inbox.list();
////            for(Folder fd:f)
////                System.out.println(">> "+fd.getName());
//
////            int cou = inbox.getMessageCount();
////            System.out.println("MSG COU -> "+cou);
//            /*MimeMessage msg;
//
//            for(int i=1;i<=cou;i++){
//                msg = (MimeMessage) inbox.getMessage(i);
//                List<Header> list = Collections.list(msg.getAllHeaders());
//                Address[] in = msg.getFrom();
//                for (Address address : in) {
//                    System.out.println("FROM:" + address.toString());
//                    String subFrom = Arrays.toString(msg.getFrom());
//                    subFrom = subFrom.substring(subFrom.indexOf('<')+1,subFrom.length()-2);
//                    System.out.println("SUB_FROM:" + subFrom);
//                    System.out.println("SUB_TO:" + Arrays.asList(msg.getRecipients(Message.RecipientType.TO)));
//
//                    System.out.println("SENT DATE:" + msg.getSentDate());
//                    System.out.println("SUBJECT:" + msg.getSubject());
//                }
//            }*/
//
////            Multipart mp = (Multipart) msg.getContent();
////            BodyPart bp = mp.getBodyPart(0);
////
////            System.out.println("CONTENT:" + bp.getContent());
//
//        } catch (Exception mex) {
//            mex.printStackTrace();
//        }
//
//    }
//
//}
