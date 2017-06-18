package com.acme.controller;

import com.acme.exception.TemplateException;
import com.acme.model.Credential;
import com.acme.model.Subject;
import com.acme.repository.CredentialRepository;
import com.acme.repository.SubjectRepository;
import com.acme.service.EmailService;
import com.acme.service.SubjectService;
import com.acme.service.TokenService;
import com.acme.util.PasswordHashing;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/public")
public class PublicController {

	@Value("${app.home}")
	private String appMainPage;
	@Value("${app.registration.result}")
	private String registrationResultPage;
	@Value("${app.restore.result}")
	private String restoreResultPage;

	@Autowired
	TokenService tokenService;

	@Autowired
	SubjectRepository subjectRepository;

	@Autowired
	SubjectService subjectService;

	@Autowired
	EmailService emailService;

	@Autowired
	PlatformTransactionManager transactionManager;

	@Autowired
	CredentialRepository credentialRepository;


	@RequestMapping(method = RequestMethod.GET, value = "/auth/*")
	public void privateOrderProcess(HttpServletRequest req, HttpServletResponse res) throws IOException, MessagingException, TemplateException {
		// получаем токен
		final String token = req.getParameter("jwt");
		res.setStatus(HttpServletResponse.SC_FOUND);

		// если пытаемся пройти без токена вообще
		if (Strings.isNullOrEmpty(token)) {
			res.sendRedirect(appMainPage);
			return;
		}
		// определим цель запроса
		String page = req.getServletPath().contains("restore") ? restoreResultPage : registrationResultPage;

		// парсим токен
		try {
			final Claims claims = Jwts.parser().setSigningKey(tokenService.getKey()).parseClaimsJws(token).getBody();
			// если токен просрочен, будет брошен exception
			if (claims.getExpiration().after(new Date())) {
				Subject subject = subjectRepository.findOne(claims.getId());
				// если не нашли клиента, то ошибка
				if (subject == null) {
					res.sendRedirect(page + "?confirmed=false");
					return;
				}
				// получаем credential
				Credential credential = credentialRepository.findOne(subject.getId());

				// если цель восстановление пароля
				if (page.contentEquals(registrationResultPage)) {
					addTokenHeader(res, credential);
					if (subject.isEnabled()) {
						// если пользователь уже активирован
						res.sendRedirect(appMainPage);
					} else {
						subject.setEnabled(true);
						subjectRepository.save(subject);
						// если регистрация прошла успешно, то отправим письмо о результате
						emailService.sendRegistrationConfirm(subject.getEmail());
						res.sendRedirect(page + "?confirmed=true");
					}
				} else {
						credential.setPassword(PasswordHashing.hashPassword(String.valueOf(claims.get("password"))));
						credentialRepository.save(credential);
						emailService.sendPassChangeConfirm(subject.getEmail());
						res.sendRedirect(page + "?confirmed=true");
				}
			}
		} catch (ExpiredJwtException e) {
			res.sendRedirect(page + "?confirmed=false");
		}

	}

	private void addTokenHeader(HttpServletResponse response, Credential credential) {
		response.addHeader("Authorization", tokenService.createToken(credential));
	}

}
