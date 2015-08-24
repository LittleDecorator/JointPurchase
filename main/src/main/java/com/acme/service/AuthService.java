package com.acme.service;

import com.acme.helper.RegistrationData;
import com.acme.helper.SubjectCredential;

public interface AuthService {

    boolean isEnabled(String login);

    boolean validate(SubjectCredential subjectCredential);

    boolean register(RegistrationData data);

}
