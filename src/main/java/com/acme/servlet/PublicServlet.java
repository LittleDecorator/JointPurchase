package com.acme.servlet;

import com.acme.config.Settings;
import com.acme.model.Subject;
import com.acme.repository.CredentialRepository;
import com.acme.repository.SubjectRepository;
import com.acme.service.TokenService;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * No generation here. Just update user status and redirect to app result page.
 * There will be login
 */
public class PublicServlet extends HttpServlet {

    @Autowired
    TokenService tokenService;

    @Autowired
    SubjectRepository subjectRepository;

//    @Autowired
//    CredentialRepository credentialRepository;

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
        res.setStatus(HttpServletResponse.SC_FOUND);

        //if no token then go to fail page
        if(Strings.isNullOrEmpty(token)){
            res.sendRedirect(Settings.appMainPage);
            return;
        }

        try {
            final Claims claims = Jwts.parser().setSigningKey(tokenService.getKey()).parseClaimsJws(token).getBody();

            if(claims.getExpiration().after(new Date())){

                /* check if it pass change */
                if(req.getPathInfo().contains("restore")){
                    res.sendRedirect(Settings.restorePage);
                } else {
                    /* registration */
                    String subjectId = claims.getId();
                    if (subjectRepository != null) {
                        Subject subject = subjectRepository.getById(subjectId);
                        if (subject == null) {
                            res.sendRedirect(Settings.registrationResultPage + "?confirmed=false");
                            return;
                        }
                        if (subject.isEnabled()) {
                            //if user already valid then go to main page (no login)
                            res.sendRedirect(Settings.appMainPage);
                        } else {
                            subject.setEnabled(true);
                            subjectRepository.updateSelectiveById(subject);
                            res.sendRedirect(Settings.registrationResultPage + "?confirmed=true");
                        }
                    }
                }
            }
        } catch (ExpiredJwtException e) {
            res.sendRedirect(Settings.registrationResultPage + "?confirmed=false");
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }


}

