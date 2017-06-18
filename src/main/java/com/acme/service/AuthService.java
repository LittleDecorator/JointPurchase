package com.acme.service;

import com.acme.exception.TemplateException;
import com.acme.helper.RegistrationData;
import com.acme.helper.SubjectCredential;
import com.acme.model.Credential;
import com.acme.model.Subject;
import io.jsonwebtoken.Claims;

import javax.mail.MessagingException;
import javax.servlet.ServletRequest;
import java.io.IOException;

public interface AuthService {

    Subject getSubject(String login);

    Credential validate(SubjectCredential subjectCredential);

    String register(RegistrationData data);

    boolean isAdmin(String username);

    Claims getClaims(ServletRequest servletRequest);

    void restore(String login, String password) throws TemplateException, IOException, MessagingException;

    String decryptPassword(String password);

    boolean confirmRequestBySms(String subjectId, String phone);

    boolean confirmBySms(String subjectId, int code);
}
