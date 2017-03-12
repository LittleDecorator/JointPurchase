package com.acme.service;

import com.acme.email.Email;
import com.acme.exception.TemplateException;
import com.acme.model.Order;
import com.acme.model.gmail.SimpleDraft;
import com.acme.model.gmail.SimpleMessage;
import com.acme.model.gmail.SimpleThread;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

/**
 * Сервис отправки email сообщений
 */
public interface EmailService {

	/**
	 * Оповещение о смене статуса заказа
	 * @param order - заказ клиента
	 * @throws IOException
	 * @throws MessagingException
	 * @throws TemplateException
	 */
	void sendOrderStatus(Order order) throws IOException, MessagingException, TemplateException;

	/**
	 * Отправка ссылки для подтверждения регистрации
	 * @param mailTo    - получатель
	 * @param tokenLink - token регистрации
	 * @return
	 */
	boolean sendRegistrationToken(String mailTo, String tokenLink);


	/**
	 * Сообщение об успешной регистрации
	 */
	void sendRegistrationConfirm(String mailTo) throws IOException, MessagingException, TemplateException;

	/**
	 * Сообщение об успешном изменении пароля
	 */
	void sendPassChangeConfirm(String mailTo, String tokenLink) throws IOException, TemplateException, MessagingException;

	/**
	 * Рассылка новостей и инфо про акции
	 */
	void sendNews(String mailTo);

	/**
	 * Получение списка входящих сообщений
	 * @return - список писем
	 * @throws MessagingException
	 */
	List<SimpleThread> getInbox() throws IOException;

	/**
	 * Получение списка отправленных сообщений
	 * @return - список отправленных сообщений
	 * @throws MessagingException
	 */
	List<SimpleThread> getSent() throws IOException;

	List<SimpleDraft> getDraft() throws IOException;

	/**
	 * Получение списка нитей в корзине
	 * @return
	 * @throws IOException
	 */
	List<SimpleThread> getTrash() throws IOException;

	/**
	 * Перемещение сообщения в корзину
	 * @param id
	 */
	void removeMessage(String id);

	/**
	 * Восстановление сообщения из корзины
	 * @param id
	 */
	void restoreMessage(String id);

	/**
	 * Перемещение Цепочки сообщений в корзину
	 * @param id
	 */
	void removeThread(String id);

	/**
	 * восстановление Цепочки сообщений из корзины
	 * @param id
	 */
	void restoreThread(String id);

	void removeDraft(String id) throws IOException;

	/**
	 * Отправка сообщения без вложений
	 * @param message
	 * @throws IOException
	 * @throws MessagingException
	 */
	void sendWithoutAttach(SimpleMessage message) throws IOException, MessagingException;

	void insertToInbox(SimpleMessage message) throws IOException, MessagingException;

	void saveDraft(SimpleDraft draft) throws IOException, MessagingException;

	SimpleDraft getDraft(String id) throws IOException;

	void sendDraft(SimpleDraft draft) throws IOException, MessagingException;
}
