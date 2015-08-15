/*
import com.acme.Application;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.MessagingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
public class GreenMailTest {

    @Resource
    private JavaMailSenderImpl emailSender;
    private GreenMail testSmtp;

    @Before
    public void testSmtpInit(){
        ServerSetup setup = new ServerSetup(65438, "localhost", "smtp");
        //Test properties
        testSmtp = new GreenMail(setup);
        //GreenMail server startup using test configuration
        testSmtp.start();
        //don't forget to set the same test port to EmailService, which is using by GreenMail
        emailSender.setPort(65438);
        emailSender.setHost("localhost");
    }
    @Test
    public void testEmail() throws InterruptedException, MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("test@sender.com");
        message.setTo("test@receiver.com");
        message.setSubject("test subject");
        message.setText("test message");
        //First we need to call the actual method of EmailSErvice
        emailSender.send(message);
        //Then after that using GreenMail need to verify mail sent or not
        assertTrue(testSmtp.waitForIncomingEmail(5000, 1));
        Message[] messages = testSmtp.getReceivedMessages();
        assertEquals(1, messages.length);
        assertEquals("test subject", messages[0].getSubject());
        String body = GreenMailUtil.getBody(messages[0]).replaceAll("=\r?\n", "");
        assertEquals("test message", body);
    }
    @After
    public void cleanup(){
        testSmtp.stop();
    }

}
*/
