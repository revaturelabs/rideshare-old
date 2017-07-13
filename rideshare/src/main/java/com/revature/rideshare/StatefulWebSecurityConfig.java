package com.revature.rideshare;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * A web security configuration that stores authentication information on the
 * server.
 * 
 * @author Eric Christie
 * @created July 13, 2017
 */
@Configuration
public class StatefulWebSecurityConfig extends WebSecurityConfigurerAdapter {

	/*
	 * NOTES: 
	 * - modify User so that it implements UserDetails 
	 * - modify UserServiceImpl so that it implements UserDetailsManager 
	 * - might need to create a custom security filter in order to handle token authentication 
	 * - set up an OAuth2RestTemplate to handle consuming the slack api 
	 * - consider turning this RideShare application into an OAuth2 authorization server 
	 * - finish configuring ssl and redirection from http to https
	 */

}
