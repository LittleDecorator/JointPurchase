package com.acme.service.impl;

import com.acme.gen.domain.Credential;
import com.acme.gen.domain.Subject;
import com.acme.gen.mapper.CredentialMapper;
import com.acme.gen.mapper.SubjectMapper;
import com.acme.helper.RegistrationData;
import com.acme.helper.SubjectCredential;
import com.acme.service.AuthService;
import com.acme.service.TokenService;
import com.acme.util.PasswordHashing;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class AuthServiceImpl implements AuthService{

    @Autowired
    SubjectMapper subjectMapper;

    @Autowired
    CredentialMapper credentialMapper;

    @Autowired
    TokenService tokenService;

    @Autowired
    EmailServiceImpl emailService;

    @Override
    public boolean isEnabled(String login) {
        Subject subject = subjectMapper.selectByPrimaryKey(login);
        return subject!=null?subject.isEnabled():null;
    }

    @Override
    public boolean validate(SubjectCredential subjectCredential) {
        if(Strings.isNullOrEmpty(subjectCredential.name) || Strings.isNullOrEmpty(subjectCredential.password)){
            return false;
        }
        if(!isEnabled(subjectCredential.name)){
            return false;
        }
        Credential credential = credentialMapper.selectByPrimaryKey(subjectCredential.name);
        if (credential == null) {
            return false;
        }
        return PasswordHashing.validatePassword(subjectCredential.password,credential.getPassword());
    }

    @Override
    public boolean isAdmin(String username) {
        return false;
    }

    @Override
    public boolean register(RegistrationData data) {
        //create subject
        Subject subject = new Subject();
        subject.setEmail(data.getMail());
        subject.setFirstName(data.getFirstName());
        subject.setLastName(data.getLastName());
        subject.setMiddleName(data.getMiddleName());
        subject.setPhoneNumber(data.getPhone());

        subjectMapper.insertSelective(subject);

        //create credential
        Credential credential = new Credential();
        credential.setSubjectId(subject.getId());
        credential.setRoleId("user");
        credential.setPassword(data.getPwd());
        credentialMapper.insertSelective(credential);

        //create temp token
        String tmpToken = tokenService.createExpToken(credential, Long.valueOf(24*60*60*1000));
        System.out.println(tmpToken);

        //send email
        String html = "<a href='http://localhost:7979/public/auth/confirm?jwt="+tmpToken+"'>Confirm test user registration</a>";
        try{
            MimeMessage message = emailService.getBuilder().setTo(data.getMail()).setSubject("Registration confirmation").setFrom("purchase@service.com").setHtmlContent(html).build();
           emailService.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
