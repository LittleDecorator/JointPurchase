package com.acme.security.xauth;

import com.acme.security.GenericTokenFilter;
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
 * Created by vkokurin on 05.03.2015.
 */
public class XAuthTokenFilter extends GenericTokenFilter {

    private final static String XAUTH_TOKEN_HEADER_NAME = "x-auth-token";

    public XAuthTokenFilter(UserDetailsService detailsService, TokenProvider tokenProvider) {
        super(detailsService, tokenProvider);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            String authToken = httpServletRequest.getHeader(XAUTH_TOKEN_HEADER_NAME);
            if (StringUtils.hasText(authToken)) {
                UsernamePasswordAuthenticationToken token = authenticate(authToken);
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
