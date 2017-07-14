//package com.revature.rideshare;
//
//import javax.servlet.Filter;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.oauth2.client.OAuth2ClientContext;
//import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
//import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
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
////@Configuration
//public class StatefulWebSecurityConfig extends WebSecurityConfigurerAdapter {
//
//	/*
//	 * NOTES: 
//	 * - modify User so that it implements UserDetails 
//	 * - modify UserServiceImpl so that it implements UserDetailsManager 
//	 * - might need to create a custom security filter in order to handle token authentication 
//	 * - set up an OAuth2RestTemplate to handle consuming the slack api 
//	 * - consider using OAuth2 or OpenID for security in this application
//	 * - finish configuring ssl and redirection from http to https
//	 * - angular-jwt allows you to set a custom request header name for your token
//	 * 
//	 * - when matching rides with requesters, individuals from the same batch should prioritized living in the same apartment
//	 */
//	
//	@Autowired
//	OAuth2ClientContext oauth2ClientContext;
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
//			.and().addFilterBefore(slackSsoFilter(), BasicAuthenticationFilter.class);
//	}
//	
//	@Override
//	public void configure(WebSecurity web) throws Exception {
//		web.ignoring().antMatchers("/css", "/images");
//	}
//	
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.authenticationProvider(authenticationProvider)
//	}
//	
//	/**
//	 * An authentication provider that processes the value of the "rideshare-auth-token"
//	 * header and returns an Authentication
//	 */
//	class RideshareTokenAuthenticationProvider implements AuthenticationProvider {
//
//		@Override
//		public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//			return null;
//		}
//
//		@Override
//		public boolean supports(Class<?> authClass) {
//			String authClassName = authClass.getName();
//			System.out.println("RideshareTokenAuthenticationProvider authentication class name = " + authClassName);
//			return authClassName.equals("java.lang.String");
//		}
//		
//	}
//	
//	private Filter slackSsoFilter(ClientResources client, String path) {
//		OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
//		
//	}
//	
//	@Bean
//	@ConfigurationProperties("slack.client")
//	public AuthorizationCodeResourceDetails slackResource() {
//		return new AuthorizationCodeResourceDetails();
//	}
//	
//	class RideshareAuthenticationManager implements AuthenticationManager {
//		@Override
//		public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//			return null;
//		}
//	}
//
//}
