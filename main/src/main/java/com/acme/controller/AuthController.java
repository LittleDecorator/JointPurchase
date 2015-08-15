package com.acme.controller;

import com.acme.gen.domain.Credential;
import com.acme.gen.domain.ItemExample;
import com.acme.gen.domain.Subject;
import com.acme.gen.domain.SubjectExample;
import com.acme.gen.mapper.CredentialMapper;
import com.acme.gen.mapper.SubjectMapper;
import com.acme.service.TokenService;
import com.acme.service.impl.EmailServiceImpl;
import com.google.common.base.Strings;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    CredentialMapper credentialMapper;

    @Autowired
    SubjectMapper subjectMapper;

    @Autowired
    TokenService tokenService;

    @Autowired
    EmailServiceImpl emailService;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public LoginResponse auth(@RequestBody UserLogin login) throws ServletException {
        System.out.println("AuthLogin: login credentials -> " + login.toString());
        if (!Strings.isNullOrEmpty(login.name) && isSubjectEnabled(login.name)){
            Credential credential = credentialMapper.selectByPrimaryKey(login.name);
            if(credential.getPassword().contentEquals(login.password)){
                return new LoginResponse(tokenService.createToken(credential));
            }
        }
//        throw new ServletException("Invalid login");
        return null;
    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public boolean register(@RequestBody String input) throws ServletException, ParseException, MessagingException, UnsupportedEncodingException {
        System.out.println(input);
        JSONParser parser=new JSONParser();
        JSONObject main = (JSONObject) parser.parse(input);
        System.out.println(main);

        ItemExample itemExample = new ItemExample();
        ItemExample.Criteria criteria= itemExample.createCriteria();

        String fio = main.get("fio").toString();
        String phone = main.get("phone").toString();
        String mail = main.get("mail").toString();
        String pwd = main.get("pwd").toString();

        //create subject
        Subject subject = new Subject();
        subject.setEmail(mail);
        subject.setFirstName(fio);
        subject.setLastName(fio);
        subject.setMiddleName(fio);
        subject.setPhoneNumber(phone);
        subject.setEnabled(false);

        subjectMapper.insertSelective(subject);

        //create credential
        Credential credential = new Credential();
        credential.setSubjectId(subject.getId());
        credential.setRoleId("user");
        credential.setPassword(pwd);
        credentialMapper.insertSelective(credential);

        //create temp token
        String tmpToken = tokenService.createExpToken(credential, Long.valueOf(24*60*60*1000));
        System.out.println(tmpToken);

        //send email
        String html = "<a href='http://localhost:7979/public/auth/confirm/test?jwt="+tmpToken+"'>Confirm test user registration</a>";
        MimeMessage message = emailService.getBuiler().setTo(mail).setSubject("Registration confirmation").setFrom("purchase@service.com").setHtmlContent(html).build();
        emailService.send(message);
        return true;
    }

    private boolean isSubjectEnabled(String login){
        //check subject
        SubjectExample example = new SubjectExample();
        example.createCriteria().andEmailEqualTo(login).andEnabledEqualTo(true);
        List<Subject> subjects = subjectMapper.selectByExample(example);
        return subjects.size()>0;
    }

    private static class UserLogin {
        public String name;
        public String password;

        @Override
        public String toString() {
            return "UserLogin{" +
                    "name='" + name + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }

    private static class LoginResponse {
        public String token;

        public LoginResponse(final String token) {
            this.token = token;
        }
    }

}
