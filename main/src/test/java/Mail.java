import com.acme.service.impl.EmailServiceImpl;
import com.acme.util.EmailBuilder;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Mail {

    public static void main(String[] args) throws Exception {
		EmailServiceImpl emailService = new EmailServiceImpl();
        Properties props = new Properties();

        props.put("mail.smtp.host", "grimmstory.ru");
        props.put("mail.debug", "true");
        props.put("mail.smtp.port", 587);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "grimmstory.ru");

        System.out.println(1);
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("bobby", "12345678");
                    }
                });
        emailService.setMailSession(session);
        EmailBuilder builder = emailService.getBuiler();
//        MimeMessage message = builder.setTo("knpdeveloper@gmail.com").setFrom("bobby@grimmstory.ru").setHtmlContent("TEST FOR GMAIL").setSubject("Registration confirmation").build();
//        MimeMessage message = builder.setTo("kobzeff.inc@mail.ru").setFrom("bobby@grimmstory.ru").setHtmlContent("TEST FOR MAIL.RU").setSubject("Registration confirmation").build();
        MimeMessage message = builder.setTo("vlapku@ya.ru").setFrom("bobby@grimmstory.ru").setHtmlContent("Test for Yandex").setSubject("Registration confirmation").build();
        emailService.send(message);
        System.out.println(1);
	}

}
