import com.acme.service.impl.EmailServiceImpl;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;
import java.util.UUID;

public class Mail {

    public static void main(String[] args) throws Exception {
		/*String toEmail = "kobzeff.inc@mail.ru";
		String fromEmail = "knpdeveloper@gmail.com";
		EmailServiceImpl i = new EmailServiceImpl();
		i.sendMail("smtp.gmail.com", "587",
				"knpdeveloper@gmail.com", "25oct87!", fromEmail, toEmail,
				"hello" + UUID.randomUUID().toString(), "this is my body", null);
		System.out.println(1);*/

        /*SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("kobzeff.inc@mail.ru");
        mailMessage.setFrom("someone@localhost");
        mailMessage.setSubject("test");
        mailMessage.setText("From fake smtp");

        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", true);
        mailProperties.put("mail.smtp.starttls.enable", true);
        mailProperties.put("mail.smtp.host", "smtp.gmail.com");
        mailProperties.put("mail.smtp.port", 587);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setJavaMailProperties(mailProperties);
        mailSender.setUsername("knpdeveloper@gmail.com");
        mailSender.setPassword("25oct87!");
        mailSender.send(mailMessage);
        System.out.println(1);*/
	}

}
