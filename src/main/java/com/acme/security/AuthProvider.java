package com.acme.security;

import com.acme.service.AuthService;
import com.acme.service.SubjectService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Authentication provider
 */
public class AuthProvider implements AuthenticationProvider {

	private final PasswordEncoder passwordEncoder;

    private final SubjectService userDetailsService;

    private AuthService authService;

    public AuthProvider(SubjectService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        String login = token.getName();
        UserDetails user = userDetailsService.loadUserByUsername(login);
        if (user == null) {
            throw new UsernameNotFoundException("User does not exists");
        }
        String password = user.getPassword();
        String tokenPassword = (String) token.getCredentials();
        //authService.validate()
        if(passwordEncoder.matches(tokenPassword, password)) {
	        return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        } else {
	        throw new BadCredentialsException("Invalid username/password");
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
