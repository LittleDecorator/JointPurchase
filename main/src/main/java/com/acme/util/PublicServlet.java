package com.acme.util;

import com.acme.gen.domain.Subject;
import com.acme.gen.mapper.SubjectMapper;
import com.acme.service.TokenService;
import io.jsonwebtoken.Claims;
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
import java.util.Date;

public class PublicServlet extends HttpServlet {

    @Autowired
    TokenService tokenService;

    @Autowired
    SubjectMapper subjectMapper;

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
        final String token = req.getParameter("jwt");
        System.out.println(token);
        final Claims claims = Jwts.parser().setSigningKey(tokenService.getKey()).parseClaimsJws(token).getBody();
        String subjectId = claims.getId();
        Date date = claims.getExpiration();
        res.setContentType("text/plain");
        if(date.getTime() > System.currentTimeMillis()){
            if(subjectMapper!=null){
                Subject subject = subjectMapper.selectByPrimaryKey(subjectId);
                if(subject.isEnabled()){
                    System.out.println(req.getContextPath());
                    res.sendRedirect("http://localhost:7979/#/");
                    return;
                }
                subject.setEnabled(true);
                subjectMapper.updateByPrimaryKeySelective(subject);
                res.getWriter().append("Hello, "+subject.getLastName()+" "+subject.getFirstName()+"\n You're now validate. Congrats!!!");
            } else {
                res.getWriter().append("Hello World\nCan't inject any service or mapper :(");
            }

        } else {
            res.getWriter().append("Sorry, token expired!");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }
}

