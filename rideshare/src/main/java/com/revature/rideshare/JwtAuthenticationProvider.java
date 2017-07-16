package com.revature.rideshare;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * A custom authentication provider that handles authenticating a request via a JSON Web Token
 * included in the Authorization header.
 * Basically, this takes a JSON Web Token from the Authorization header and transforms it into
 * an Authentication object containing the authenticated User if the token is valid.
 * @author Eric Christie
 * @created July 15, 2017
 */
//@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return false;
	}

}
