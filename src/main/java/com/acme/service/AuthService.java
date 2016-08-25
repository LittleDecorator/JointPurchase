package com.acme.service;

import com.acme.helper.RegistrationData;
import com.acme.helper.SubjectCredential;
import com.acme.model.Credential;
import com.acme.model.Subject;
import io.jsonwebtoken.Claims;

import javax.servlet.ServletRequest;

public interface AuthService {

    Subject getSubject(String login);

    Credential validate(SubjectCredential subjectCredential);

    boolean register(RegistrationData data);

    boolean isAdmin(String username);

    Claims getClaims(ServletRequest servletRequest);

    void restore(String login);

    String decryptPassword(String password);
}
