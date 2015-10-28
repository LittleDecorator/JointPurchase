package com.acme.service;

import com.acme.helper.RegistrationData;
import com.acme.helper.SubjectCredential;
import io.jsonwebtoken.Claims;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public interface AuthService {

    boolean isEnabled(String login);

    boolean validate(SubjectCredential subjectCredential);

    boolean register(RegistrationData data);

    boolean isAdmin(String username);

    Claims getClaims(ServletRequest servletRequest);

}
