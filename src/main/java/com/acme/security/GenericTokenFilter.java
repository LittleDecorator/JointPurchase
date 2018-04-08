package com.acme.security;

import com.acme.security.xauth.TokenProvider;
import com.acme.service.SubjectService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Filter for token validation
 */
public abstract class GenericTokenFilter extends GenericFilterBean {

    protected SubjectService detailsService;

    protected TokenProvider tokenProvider;

    public GenericTokenFilter(SubjectService detailsService, TokenProvider tokenProvider) {
        this.detailsService = detailsService;
        this.tokenProvider = tokenProvider;
    }

    protected UsernamePasswordAuthenticationToken authenticate(String authToken){
        if (this.tokenProvider.validateToken(authToken)) {
            String userId = this.tokenProvider.getUserIdFromToken(authToken);
            UserDetails details = this.detailsService.loadUserByUsername(userId);
            return new UsernamePasswordAuthenticationToken(details, details.getPassword(), details.getAuthorities());
        }
        return null;
    }

}
