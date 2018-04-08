package com.acme.security.xauth;

import com.acme.service.SubjectService;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * TokenAuth Configurator
 */
public class XAuthTokenConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

		private TokenProvider tokenProvider;
		private SubjectService userDetailsService;

		public XAuthTokenConfigurer(SubjectService userDetailsService, TokenProvider tokenProvider) {
				this.userDetailsService = userDetailsService;
				this.tokenProvider = tokenProvider;
		}

		@Override
		public void configure(HttpSecurity builder) throws Exception {
				XAuthTokenFilter filter = new XAuthTokenFilter(userDetailsService, tokenProvider);
				// add token filter before security auth filter
				builder.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
		}
}
