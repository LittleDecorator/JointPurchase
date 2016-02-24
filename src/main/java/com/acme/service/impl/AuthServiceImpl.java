package com.acme.service.impl;

import com.acme.exception.InvalidRequestException;
import com.acme.helper.RegistrationData;
import com.acme.helper.SubjectCredential;
import com.acme.model.Credential;
import com.acme.model.Subject;
import com.acme.repository.CredentialRepository;
import com.acme.repository.SubjectRepository;
import com.acme.service.AuthService;
import com.acme.service.TokenService;
import com.acme.util.PasswordHashing;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

@Service
public class AuthServiceImpl implements AuthService{

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    CredentialRepository credentialRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    EmailServiceImpl emailService;

    @Override
    public Subject getSubject(String login) {
        return subjectRepository.getByEmail(login);
//        if(subjects.size()>0){
//            return subjects.get(0);
//        } else {
//            return null;
//        }

    }

    @Override
    public Credential validate(SubjectCredential subjectCredential) {
        if (Strings.isNullOrEmpty(subjectCredential.name) || Strings.isNullOrEmpty(subjectCredential.password)) {
            return null;
        }
        Subject subject = getSubject(subjectCredential.name);
        if (!(subject != null && subject.isEnabled())) {
            return null;
        }
        Credential credential = credentialRepository.getById(subject.getId());
        if(credential != null && PasswordHashing.validatePassword(subjectCredential.password, credential.getPassword())){
            return credential;
        } else {
            return null;
        }
    }

    @Override
    public boolean isAdmin(String username) {
        return false;
    }

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
                System.out.println("JWT FIlter -> "+claims.toString());
                return claims;
            }
            catch (final SignatureException e) {
                System.out.println("Invalid token.");
                return null;
            }
        }
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
        subjectRepository.insert(subject);

        //create credential
        Credential credential = new Credential();
        credential.setSubjectId(subject.getId());
        credential.setRoleId("user");
        credential.setPassword(PasswordHashing.hashPassword(data.getPassword()));
        credentialRepository.insert(credential);

        //create temp token
        String tmpToken = tokenService.createExpToken(credential, (long) (24 * 60 * 60 * 1000));
        System.out.println(tmpToken);

        //send email
        String html = "<a href='http://grimmstory.ru/public/auth/confirm?jwt="+tmpToken+"'>Confirm registration</a>";
        return emailService.sendRegistrationToken(data.getMail(),html);
    }

    @Override
    public void restore(String login) {
        //TODO: Need update exception handler. Add custom error interface and different implementations for all situations
        System.out.println("In restore");
        Subject subject = getSubject(login);
        if(subject!=null){
            System.out.println(subject.getEmail()+" "+subject.getFirstName());
            String tmpToken = tokenService.createExpToken(credentialRepository.getById(subject.getId()), (long) (24 * 60 * 60 * 1000));
            System.out.println(tmpToken);

            //send email
            String html = "<a href='http://grimmstory.ru/public/auth/restore?jwt="+tmpToken+"'>Change password</a>";
            emailService.sendRegistrationToken(subject.getEmail(),html);
//            return subject.getEmail();
        } else {
            System.out.println("Subject not found");
            throw new InvalidRequestException("Subject not found", login);
//            return null;
        }
    }
}
