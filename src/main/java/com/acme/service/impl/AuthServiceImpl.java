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
import com.acme.service.EmailService;
import com.acme.service.SmsService;
import com.acme.service.TokenService;
import com.acme.util.PasswordHashing;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class AuthServiceImpl implements AuthService{

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
        Credential credential = credentialRepository.getById(subject.getId());
        if(credential != null && PasswordHashing.validatePassword(subjectCredential.getPassword(), credential.getPassword())){
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
    public String decryptPassword(String password){
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
            }
            catch (final SignatureException e) {
                System.out.println("Invalid token.");
                return null;
            }
        }
    }

    /**
     * Регистрация нового пользователя.
     * Новый пользователь будет не активированным, поэтому он не сможет авторизовываться.
     * @param data
     * @return
     */
    @Override
    public boolean register(RegistrationData data) {
        //create subject
        Subject subject = new Subject();
        subject.setEmail(data.getMail());
        subject.setFirstName(data.getFirstName());
        subject.setLastName(data.getLastName());
        subject.setMiddleName(data.getMiddleName());
        subject.setPhoneNumber(data.getPhone());
        subjectRepository.save(subject);

        //create credential
        Credential credential = new Credential();
        credential.setSubjectId(subject.getId());
        credential.setRoleId("user");
        credential.setPassword(PasswordHashing.hashPassword(data.getPassword()));
        credentialRepository.insert(credential);

        //create temp token
        String tmpToken = tokenService.createExpToken(credential, (long) (24 * 60 * 60 * 1000));
//        String tokenLink = "http://grimmstory.ru/public/auth/confirm?jwt="+tmpToken;
        String tokenLink = "http://192.168.1.88:7777/public/auth/confirm?jwt="+tmpToken;
        return emailService.sendRegistrationToken(data.getMail(), tokenLink);
    }

    /**
     * Обработка запроса пользователя на изменение пароля
     * @param login
     */
    @Override
    public void restore(String login) throws TemplateException, IOException, MessagingException {
        Subject subject = getSubject(login);
        if(subject!=null){
            if(!Strings.isNullOrEmpty(subject.getPhoneNumber())){
                /* будем использовать смс если есть тел номер у клиента */
                smsService.passChangeConfirm();
            } else {
                /* будем использовать почту */
                String tmpToken = tokenService.createExpToken(credentialRepository.getById(subject.getId()), (long) (24 * 60 * 60 * 1000));
                String tokenLink = "http://grimmstory.ru/public/auth/restore?jwt="+tmpToken;
                emailService.sendPassChangeConfirm(subject.getEmail(), tokenLink);
            }
        } else {
            System.out.println("Subject not found");
            throw new InvalidRequestException("Subject not found", login);
        }
    }
}
