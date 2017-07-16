package com.revature.rideshare;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * A stateless web security configuration that uses token-based authentication
 * @author Eric Christie
 * @created July 13, 2017
 */
//@Configuration
public class StatelessWebSecurityConfig extends WebSecurityConfigurerAdapter {
	
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.authenticationProvider(authenticationProvider)
//	}
	
//	class RideshareAuthenticationManager implements AuthenticationManager {
//	@Override
//	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//		return null;
//	}
//}

}
