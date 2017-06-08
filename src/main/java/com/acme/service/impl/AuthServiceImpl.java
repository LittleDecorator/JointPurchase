package com.acme.service.impl;

import com.acme.exception.InvalidRequestException;
import com.acme.exception.TemplateException;
import com.acme.helper.RegistrationData;
import com.acme.helper.SubjectCredential;
import com.acme.model.Credential;
import com.acme.model.Subject;
import com.acme.repository.CredentialRepository;
import com.acme.repository.SubjectRepository;
import com.acme.service.AuthService;
import com.acme.service.ConfirmService;
import com.acme.service.EmailService;
import com.acme.service.SmsService;
import com.acme.service.TokenService;
import com.acme.util.PasswordHashing;
import com.google.api.client.util.Maps;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

	@Value("${app.home}")
	String HOME;

	@Value("${app.host}")
	String HOST;

	@Autowired
	SubjectRepository subjectRepository;

	@Autowired
	CredentialRepository credentialRepository;

	@Autowired
	TokenService tokenService;

	@Autowired
	EmailService emailService;

	@Autowired
	SmsService smsService;

	@Autowired
	ConfirmService confirmService;

	/**
	 * Получение пользователя по логину
	 * @param login
	 * @return
	 */
	@Override
	public Subject getSubject(String login) {
		return subjectRepository.findByEmail(login);
	}

	/**
	 * Проверка корректности указанных пользователем логина/пароля
	 * @param subjectCredential
	 * @return
	 */
	@Override
	public Credential validate(SubjectCredential subjectCredential) {
		if (Strings.isNullOrEmpty(subjectCredential.getName()) || Strings.isNullOrEmpty(subjectCredential.getPassword())) {
			return null;
		}
		Subject subject = getSubject(subjectCredential.getName());
		if (!(subject != null && subject.isEnabled())) {
			return null;
		}
		Credential credential = credentialRepository.findOne(subject.getId());
		if (credential != null && PasswordHashing.validatePassword(subjectCredential.getPassword(), credential.getPassword())) {
			return credential;
		} else {
			return null;
		}
	}

	/**
	 * Дешифровка пароля пользователя, пришедшего с клиента
	 * @param password
	 * @return
	 */
	@Override
	public String decryptPassword(String password) {
		try {
			SecretKey key = new SecretKeySpec(Base64.decodeBase64("ZQiPJvvwFlfa9IxXj+F+eJCST+XvFr6nWYS0rloQZdQ="), "AES");
			AlgorithmParameterSpec iv = new IvParameterSpec(Base64.decodeBase64("ksgfrrfixQ4xLk/qV5CmRg=="));

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, iv);

			return new String(cipher.doFinal(Base64.decodeBase64(password)), "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException("This should not happen in production.", e);
		}
	}

	@Override
	public boolean isAdmin(String username) {
		return false;
	}

	/**
	 * Получение полезной информации из token'а
	 * @param servletRequest
	 * @return
	 */
	@Override
	public Claims getClaims(ServletRequest servletRequest) {
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		final String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			System.out.println("Missing or invalid Authorization header.");
			return null;
		} else {
			final String token = authHeader.substring(7); // The part after "Bearer "

			try {
				final Claims claims = Jwts.parser().setSigningKey(tokenService.getKey()).parseClaimsJws(token).getBody();
				return claims;
			} catch (final SignatureException e) {
				System.out.println("Invalid token.");
				return null;
			}
		}
	}

	/**
	 * Регистрация нового пользователя.
	 * Новый пользователь будет не активированным, поэтому он не сможет авторизовываться.
	 * @param data
	 * @return ID созданного субъекта (клиента)
	 */
	@Override
	public String register(RegistrationData data) {
		Subject subject = getSubject(data.getMail());
		Credential credential;
		if (subject != null) {
			if(subject.isEnabled()){
				throw new InvalidRequestException("Пользователь с указанным адресом уже зарегистрирован", data.getMail());
			}
			// обовим существующую не активную запись при повторной регистрации
			subject.setFirstName(data.getFirstName());
			subject.setLastName(data.getLastName());
			subject = subjectRepository.save(subject);

			credential = credentialRepository.findOne(subject.getId());
			credential.setPassword(PasswordHashing.hashPassword(decryptPassword(data.getPassword())));
			credential = credentialRepository.save(credential);
		} else {
			// создадим нового пользователя
			subject = new Subject();
			subject.setEmail(data.getMail());
			subject.setFirstName(data.getFirstName());
			subject.setLastName(data.getLastName());
			subject = subjectRepository.save(subject);

			// создадим для него credential
			credential = new Credential();
			credential.setSubjectId(subject.getId());
			credential.setRoleId("user");
			credential.setPassword(PasswordHashing.hashPassword(decryptPassword(data.getPassword())));
			credential = credentialRepository.save(credential);
		}

		return subject.getId();

//		// создадим token для подтверждения
//		String tmpToken = tokenService.createExpToken(credential, (long) (24 * 60 * 60 * 1000));
//		// создадим внешнюю ссылку на наш ресурс
//		String tokenLink = HOST +"/public/auth/confirm?jwt=" + tmpToken;
//		return !Boolean.FALSE.equals(emailService.sendRegistrationToken(data.getMail(), tokenLink));
	}

	/**
	 * Отправка SMS с кодом подтверждения регистрации
	 * @param subjectId
	 * @param phone
	 * @return
	 */
	@Override
	public boolean confirmBySms(String subjectId, String phone) {
		Subject subject = subjectRepository.findByIdAndEnabledFalse(subjectId);
		if (subject == null) {
			throw new InvalidRequestException("Пользователь не запрашивал регистрацию или уже активирован", subjectId);
		}
		return !Boolean.FALSE.equals(smsService.sendSimple(phone, "Code: "+ confirmService.generateSmsCode(subjectId)));
	}

	/**
	 * Обработка запроса пользователя на изменение пароля
	 * @param login
	 */
	@Override
	public void restore(String login, String password) throws TemplateException, IOException, MessagingException {
		// получение клиента
		Subject subject = getSubject(login);
		if (subject == null) {
			throw new InvalidRequestException("Пользователь не зарегистрирован", login);
		}
		Credential credential = credentialRepository.findOne(subject.getId());
		Map<String, Object> claims = Maps.newHashMap();
		claims.put("password", password);

		if (!Strings.isNullOrEmpty(subject.getPhoneNumber())) {
			// будем использовать смс если есть тел номер у клиента
			smsService.passChangeConfirm();
		} else {
			// будем использовать почту
			String tmpToken = tokenService.createExpToken(credential, (long) (24 * 60 * 60 * 1000), claims);
			// создадим внешнюю ссылку на наш ресурс
			String tokenLink = HOST+"/public/auth/restore?jwt=" + tmpToken;
			emailService.sendPassChangeToken(subject.getEmail(), tokenLink);
		}
	}
}
