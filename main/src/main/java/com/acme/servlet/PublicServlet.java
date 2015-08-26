package com.acme.servlet;

import com.acme.gen.domain.Credential;
import com.acme.gen.domain.Subject;
import com.acme.gen.mapper.CredentialMapper;
import com.acme.gen.mapper.SubjectMapper;
import com.acme.service.TokenService;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class PublicServlet extends HttpServlet {

    @Autowired
    TokenService tokenService;

    @Autowired
    SubjectMapper subjectMapper;

    @Autowired
    CredentialMapper credentialMapper;

    private WebApplicationContext springContext;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
        final AutowireCapableBeanFactory beanFactory = springContext.getAutowireCapableBeanFactory();
        beanFactory.autowireBean(this);
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        //first parse temporary token
        final String token = req.getParameter("jwt");
        System.out.println(token);
        //if no token then go to fail page
        if(Strings.isNullOrEmpty(token)){
            res.sendRedirect("http://localhost:7979/#/registration/result?confirmed=0");
            return;
        }
        System.out.println("Token not null");
        try{
            final Claims claims = Jwts.parser().setSigningKey(tokenService.getKey()).parseClaimsJws(token).getBody();
            String subjectId = claims.getId();
            if(subjectMapper!=null){
                Subject subject = subjectMapper.selectByPrimaryKey(subjectId);
                System.out.println("Subject "+(subject == null?"NULL":"NOT NULL"));
                if(subject == null){
                    res.sendRedirect("http://localhost:7979/#/registration/result?confirmed=false");
                    return;
                }
                if(subject.isEnabled()){
                    System.out.println("Subject fine -> go main");
                    //if user already valid then go to main page (no login)
                    System.out.println(req.getContextPath());
                    res.sendRedirect("http://localhost:7979/#/");
                } else {
                    System.out.println("Subject need be enabled");
                    //enable subject and generate new correct token
                    subject.setEnabled(true);
                    subjectMapper.updateByPrimaryKeySelective(subject);
                    Credential credential = credentialMapper.selectByPrimaryKey(subject.getId());
                    PrintWriter writer = res.getWriter();
                    writer.append(tokenService.createToken(credential));
//                    writer.flush();
//                    writer.close();
                    res.sendRedirect("http://localhost:7979/#/registration/result?confirmed=true");
                }
//                res.getWriter().append("Hello, "+subject.getLastName()+" "+subject.getFirstName()+"\n You're now validate. Congrats!!!");
            }
        } catch (ExpiredJwtException e){
            System.err.println("Token expired");
            res.sendRedirect("http://localhost:7979/#/registration/result?confirmed=false");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }


}

