package com.acme.security.xauth;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created by vkokurin on 05.03.2015.
 */
public class XAuthTokenConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

		private TokenProvider tokenProvider;
		private UserDetailsService userDetailsService;

		public XAuthTokenConfigurer(UserDetailsService userDetailsService, TokenProvider tokenProvider) {
				this.userDetailsService = userDetailsService;
				this.tokenProvider = tokenProvider;
		}

		@Override
		public void configure(HttpSecurity builder) throws Exception {
				XAuthTokenFilter filter = new XAuthTokenFilter(userDetailsService, tokenProvider);
				builder.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
		}
}
