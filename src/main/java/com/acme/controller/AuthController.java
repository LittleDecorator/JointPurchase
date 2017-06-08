package com.acme.controller;

import com.acme.annotation.JsonAttribute;
import com.acme.exception.TemplateException;
import com.acme.helper.LoginResponse;
import com.acme.helper.RegistrationData;
import com.acme.helper.SubjectCredential;
import com.acme.model.Credential;
import com.acme.repository.CredentialRepository;
import com.acme.service.AuthService;
import com.acme.service.TokenService;
import com.acme.util.PasswordHashing;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    CredentialRepository credentialRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    AuthService authService;

    /**
     * Авторизация клиента
     * @param subjectCredential
     * @return
     * @throws ServletException
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public LoginResponse authentication(@RequestBody SubjectCredential subjectCredential) throws ServletException {
        subjectCredential.setPassword(authService.decryptPassword(subjectCredential.getPassword()));
        Credential credential = authService.validate(subjectCredential);
        if (credential!=null){
            return new LoginResponse(tokenService.createToken(credential));
        }
        return null;
    }

    /**
     * Запрос на регистрацию нового клиента
     * @param input
     * @return
     * @throws ServletException
     * @throws ParseException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String registrationConfirm(@RequestBody RegistrationData input) throws ServletException, ParseException, UnsupportedEncodingException {
        return authService.register(input);
    }

    /**
     * Запрос на подтверждение регистрации.
     * Используется если если по каким-то причинам не прошла стандартная
     * @param subjectId - ID клиента запрашивающего подтверждения регистрации
     * @param type - тип выбранного подтверждения (sms или email). По умолчанию используется SMS
     * @return
     * @throws ServletException
     * @throws ParseException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/register/confirm/request/{type}",method = RequestMethod.POST)
    public boolean registrationConfirm(@JsonAttribute("id") String subjectId, @JsonAttribute("phone") String phone, @PathVariable("type") String type) throws ServletException, ParseException, UnsupportedEncodingException {
        //TODO: пока только SMS, то должно быть разделению по типу
        return authService.confirmBySms(subjectId, phone);
    }

    /**
     * Запрос на восстановление доступа через смену пароля
     * @param credential
     * @throws ServletException
     * @throws ParseException
     * @throws IOException
     * @throws TemplateException
     * @throws MessagingException
     */
    @RequestMapping(value = "/restore",method = RequestMethod.POST)
    public void restorePasswordConfirm(@RequestBody SubjectCredential credential) throws ServletException, ParseException, IOException, TemplateException, MessagingException {
        authService.restore(credential.getName(), authService.decryptPassword(credential.getPassword()));
    }

    /**
     * Сиена пароля
     * @param oldPassword
     * @param newPassword
     * @return
     * @throws ServletException
     * @throws ParseException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/change",method = RequestMethod.POST)
    public Boolean changePassword(@JsonAttribute("oldPassword") String oldPassword, @JsonAttribute("newPassword")String newPassword) throws ServletException, ParseException, UnsupportedEncodingException {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) attributes).getRequest();
        Credential credential = credentialRepository.findOne(authService.getClaims(servletRequest).getId());
        System.out.println(credential);
        if(credential != null && PasswordHashing.validatePassword(oldPassword, credential.getPassword())){
            credential.setPassword(PasswordHashing.hashPassword(authService.decryptPassword(newPassword)));
            credentialRepository.save(credential);
        }
        return Boolean.TRUE;
    }

}
