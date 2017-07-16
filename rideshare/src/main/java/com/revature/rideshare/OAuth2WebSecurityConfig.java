package com.revature.rideshare;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.filter.CompositeFilter;

/**
 * A web security configuration that stores authentication information on the
 * server.
 * 
 * @author Eric Christie
 * @created July 13, 2017
 */
//@Configuration
//@EnableOAuth2Client
//@EnableAuthorizationServer
public class OAuth2WebSecurityConfig extends WebSecurityConfigurerAdapter {

	/*
	 * NOTES: 
	 * - modify User so that it implements UserDetails 
	 * - modify UserServiceImpl so that it implements UserDetailsManager 
	 * - might need to create a custom security filter in order to handle token authentication 
	 * - set up an OAuth2RestTemplate to handle consuming the slack api 
	 * - consider using OAuth2 or OpenID for security in this application
	 * - finish configuring ssl and redirection from http to https
	 * - angular-jwt allows you to set a custom request header name for your token
	 * 
	 * - when matching rides with requesters, individuals from the same batch should prioritized living in the same apartment
	 */
	
	@Autowired
	OAuth2ClientContext oauth2ClientContext;
	
	@Value("${server.http.port}")
	private String httpPort;
	@Value("${server.port}")
	private String httpsPort;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.portMapper().http(Integer.parseInt(httpPort)).mapsTo(Integer.parseInt(httpsPort));
//		http.portMapper().http(8080).mapsTo(8443);

//		http.requiresChannel().antMatchers("/**").requiresSecure();
		
		http.authorizeRequests()
				.antMatchers("/admin**").hasRole("ADMIN")
				.antMatchers("/login**", "/app.bundle.js", "/partials/slackLogin.html", "/auth/check").permitAll()
				.anyRequest().authenticated()
			.and().logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/")
				.clearAuthentication(true)
				.invalidateHttpSession(true)
				.permitAll()
			.and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			.and().addFilterBefore(slackSsoFilter(), BasicAuthenticationFilter.class);
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css", "/images");
	}
	
	@Bean
	public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}
	
	private Filter slackSsoFilter() {
		CompositeFilter filter = new CompositeFilter();
		List<Filter> filters = new ArrayList<>();
		filters.add(ssoFilter(slack(), "/login/slack"));
		filter.setFilters(filters);
		return filter;
	}
	
	private Filter ssoFilter(ClientResources client, String path) {
		OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
		OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
		filter.setRestTemplate(template);
		UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(),
				client.getClient().getClientId());
		tokenServices.setRestTemplate(template);
		filter.setTokenServices(tokenServices);
		return filter;
	}
	
	@Bean
	@ConfigurationProperties("slack")
	public ClientResources slack() {
		return new ClientResources();
	}
	
	class ClientResources {
		@NestedConfigurationProperty
		private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();
		
		@NestedConfigurationProperty
		private ResourceServerProperties resource = new ResourceServerProperties();

		public AuthorizationCodeResourceDetails getClient() {
			return client;
		}

		public ResourceServerProperties getResource() {
			return resource;
		}
	}

}
