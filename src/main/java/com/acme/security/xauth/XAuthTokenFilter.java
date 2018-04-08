package com.acme.security.xauth;

import com.acme.security.GenericTokenFilter;
import com.acme.service.SubjectService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;

/**
 * Token filter itself
 */
public class XAuthTokenFilter extends GenericTokenFilter {

    //private final static String XAUTH_TOKEN_HEADER_NAME = "x-auth-token";
    private final static String XAUTH_TOKEN_HEADER_NAME = "authorization";

    public XAuthTokenFilter(SubjectService detailsService, TokenProvider tokenProvider) {
        super(detailsService, tokenProvider);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            String authHeader = httpServletRequest.getHeader(XAUTH_TOKEN_HEADER_NAME);
            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                UsernamePasswordAuthenticationToken token = authenticate(authHeader.substring(7));
                if(token != null){
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
