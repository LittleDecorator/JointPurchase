package com.acme.controller;

import com.acme.exception.TemplateException;
import com.acme.model.dto.EmailDto;
import com.acme.model.gmail.SimpleDraft;
import com.acme.model.gmail.SimpleMessage;
import com.acme.model.gmail.SimpleThread;
import com.acme.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    private EmailService emailService;

	/**
	 * Получение входящих
	 * @return
	 * @throws IOException
	 */
    @RequestMapping("/inbox")
    public List<SimpleThread> getInbox() throws IOException {
		return emailService.getInbox();
    }

	/**
	 * Получение сообщений в корзине
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/trash")
    public List<SimpleThread> getTrash() throws IOException {
		return emailService.getTrash();
    }

	/**
	 * Получение отправленных
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/sent")
	public List<SimpleThread> getSent() throws IOException {
		return emailService.getSent();
	}

	/**
	 * Получение всех черновиков
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/draft")
	public List<SimpleDraft> getDraft() throws IOException {
		return emailService.getDraft();
	}

	/**
	 * Получение черновика по ID
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/draft/{id}")
	public SimpleDraft getDraft(@PathVariable("id") String id) throws IOException {
		return emailService.getDraft(id);
	}

	/**
	 * Удаление черновика
	 * @param id
	 * @throws IOException
	 */
	@RequestMapping(value = "/draft/{id}", method = RequestMethod.DELETE)
	public void removeDraft(@PathVariable("id") String id) throws IOException {
		emailService.removeDraft(id);
	}

	/**
	 * Перемещение сообщения в корзину
	 * @param id
	 */
	@RequestMapping(value = "/message/{id}", method = RequestMethod.DELETE)
	public void removeMessage(@PathVariable("id") String id){
		emailService.removeMessage(id);
	}

	/**
	 * Возвращение сообщения из корзины
	 * @param id
	 */
	@RequestMapping(value = "/message/{id}", method = RequestMethod.PUT)
	public void restoreMessage(@PathVariable("id") String id){
		emailService.restoreMessage(id);
	}

	/**
	 * Перемещение цепочки сообщений в корзину
	 * @param id
	 */
	@RequestMapping(value = "/thread/{id}", method = RequestMethod.DELETE)
	public void removeThread(@PathVariable("id") String id){
		emailService.removeThread(id);
	}

	/**
	 * Восстановление цепочки сообщений из корзины
	 * @param id
	 */
	@RequestMapping(value = "/thread/{id}", method = RequestMethod.PUT)
	public void restoreThread(@PathVariable("id") String id){
		emailService.restoreThread(id);
	}

	/**
	 * Отправка сообщения
	 * @param message
	 * @throws IOException
	 * @throws MessagingException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void send(@RequestBody SimpleMessage message) throws IOException, MessagingException {
		emailService.sendWithoutAttach(message);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/simple")
	public void sendSimple(@RequestBody EmailDto dto) throws IOException, MessagingException, TemplateException {
		emailService.sendSimple(dto.getTo(), dto.getSubject(), dto.getBody());
	}

	/**
	 * Добавление письма во входящие
	 * @param message
	 * @throws IOException
	 * @throws MessagingException
	 */
	@RequestMapping(value = "/inbox", method = RequestMethod.POST)
	public void insert(@RequestBody SimpleMessage message) throws IOException, MessagingException {
		emailService.insertToInbox(message);
	}

	/**
	 * Сохранение черновика
	 * @param draft
	 * @throws IOException
	 * @throws MessagingException
	 */
	@RequestMapping(value = "/draft", method = RequestMethod.POST)
	public void save(@RequestBody SimpleDraft draft) throws IOException, MessagingException {
		emailService.saveDraft(draft);
	}

	/**
	 * Отправка черновика
	 * @param draft
	 * @throws IOException
	 * @throws MessagingException
	 */
	@RequestMapping(value = "/draft/{id}", method = RequestMethod.POST)
	public void sendDraft(@RequestBody SimpleDraft draft) throws IOException, MessagingException {
		emailService.sendDraft(draft);
	}

//    @RequestMapping("/mail/registration/done")
//    @ResponseStatus(HttpStatus.CREATED)
//    public void sendRegistrationDone(String recipient){
//        notifySend(recipient, Constants.REGISTRATION_DONE, "Registration confirmation");
//    }
//
//    @RequestMapping("/mail/order/create")
//    @ResponseStatus(HttpStatus.CREATED)
//    public void sendOrderCreate(String recipient){
//        notifySend(recipient,Constants.ORDER_CREATE,"Order confirmation");
//    }
//
//    private void notifySend(String recipient,String mailContent,String subject) {
//        EmailBuilder builder = emailService.getBuilder(emailService.createSession("bobby", "12345678"));
//        try{
//            MimeMessage message = builder.setTo(recipient).setFrom(emailService.getRobotCredential()).setHtmlContent(mailContent).setSubject(subject).build();
//            emailService.send(message);
//        } catch (MessagingException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//    }

}
