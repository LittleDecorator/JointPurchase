package com.acme.service;

import com.acme.gen.domain.Credential;
import com.acme.gen.domain.Subject;
import com.acme.helper.RegistrationData;
import com.acme.helper.SubjectCredential;
import io.jsonwebtoken.Claims;

import javax.servlet.ServletRequest;

public interface AuthService {

    Subject getSubject(String login);

    Credential validate(SubjectCredential subjectCredential);

    boolean register(RegistrationData data);

    boolean isAdmin(String username);

    Claims getClaims(ServletRequest servletRequest);

//    String restore(String login);
    void restore(String login);
}
