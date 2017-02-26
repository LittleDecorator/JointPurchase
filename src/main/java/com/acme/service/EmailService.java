package com.acme.service;

import com.acme.email.Email;
import com.acme.exception.TemplateException;
import com.acme.model.Order;

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
	List<Email> getInbox() throws MessagingException;

	/**
	 * Получение списка отправленных сообщений
	 * @return - список отправленных сообщений
	 * @throws MessagingException
	 */
	List<Email> getOutbox() throws MessagingException;

}
