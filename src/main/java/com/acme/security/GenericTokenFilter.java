package com.acme.security;

import com.acme.security.xauth.TokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.GenericFilterBean;

/**
 * @author Y. Tyurin <tyurin23@gmail.com> 23.07.15
 */
public abstract class GenericTokenFilter extends GenericFilterBean {

    protected UserDetailsService detailsService;

    protected TokenProvider tokenProvider;

    public GenericTokenFilter(UserDetailsService detailsService, TokenProvider tokenProvider) {
        this.detailsService = detailsService;
        this.tokenProvider = tokenProvider;
    }

    protected UsernamePasswordAuthenticationToken authenticate(String authToken){
        String username = this.tokenProvider.getUserIdFromToken(authToken);
        UserDetails details = this.detailsService.loadUserByUsername(username);
        if (this.tokenProvider.validateToken(authToken, details)) {
            return new UsernamePasswordAuthenticationToken(details, details.getPassword(), details.getAuthorities());
        }
        return null;
    }

    protected boolean isMatched(String uri, String[] matchedUri){
        for(String match : matchedUri){
            if(uri.contains(match))
                return true;
        }
        return false;
    }
}
