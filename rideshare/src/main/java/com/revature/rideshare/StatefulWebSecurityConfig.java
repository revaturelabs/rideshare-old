//package com.revature.rideshare;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//
///**
// * A web security configuration that stores authentication information on the
// * server.
// * 
// * @author Eric Christie
// * @created July 13, 2017
// */
//@Configuration
//public class StatefulWebSecurityConfig extends WebSecurityConfigurerAdapter {
//
//	/*
//	 * NOTES: 
//	 * - modify User so that it implements UserDetails 
//	 * - modify UserServiceImpl so that it implements UserDetailsManager 
//	 * - might need to create a custom security filter in order to handle token authentication 
//	 * - set up an OAuth2RestTemplate to handle consuming the slack api 
//	 * - consider turning this RideShare application into an OAuth2 authorization server 
//	 * - finish configuring ssl and redirection from http to https
//	 * - angular-jwt allows you to set a custom request header name for your token
//	 * 
//	 * - when matching rides with requesters, individuals from the same batch should prioritized living in the same apartment
//	 */
//	
//	@Value("${server.http.port}")
//	private String httpPort;
//	@Value("${server.port}")
//	private String httpsPort;
//	
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.portMapper().http(Integer.parseInt(httpPort)).mapsTo(Integer.parseInt(httpsPort));
////		http.portMapper().http(8080).mapsTo(8443);
//
////		http.requiresChannel().antMatchers("/**").requiresSecure();
//		
//		http.authorizeRequests()
//				.antMatchers("/admin**").hasRole("ADMIN")
//				.antMatchers("/login**", "/app.bundle.js", "/partials/slackLogin.html", "/auth/check").permitAll()
//				.anyRequest().authenticated()
//			.and().logout()
//				.logoutSuccessUrl("/")
//				.clearAuthentication(true)
//				.invalidateHttpSession(true)
//				.permitAll()
//			.and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//			.and().addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
//	}
//	
//	@Override
//	public void configure(WebSecurity web) throws Exception {
//		web.ignoring().antMatchers("/css", "/images");
//	}
//	
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		
//	}
//
//}
