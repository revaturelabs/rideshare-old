package com.revature.rideshare;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.filter.CompositeFilter;

import com.revature.rideshare.security.ClientResource;
import com.revature.rideshare.security.RideshareAuthoritiesExtractor;
import com.revature.rideshare.security.RidesharePrincipalExtractor;

/**
 *
 * @author Eric Christie
 * @created July 9, 2017
 */
@Configuration
@EnableWebSecurity
@EnableOAuth2Client
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	OAuth2ClientContext oauth2ClientContext;

//	@Value("${server.http.port}")
//	private String httpPort;
//	@Value("${server.port}")
//	private String httpsPort;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		http.portMapper().http(Integer.parseInt(httpPort)).mapsTo(Integer.parseInt(httpsPort));

//		http.requiresChannel().antMatchers("/**").requiresSecure();
		
		http.antMatcher("/**")
			.authorizeRequests()
				.antMatchers("/admin**").hasRole("ADMIN")
				.antMatchers("/car**", "/ride**", "/user**", "/poiController**").hasRole("USER")
				.antMatchers("/auth/identity**").authenticated()
				.antMatchers("/", "/index.html", "/app.bundle.js", "/auth/check**", "/login**", "/slack**").permitAll()
			.and().logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/")
				.clearAuthentication(true)
				.invalidateHttpSession(true)
				.permitAll()
			.and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			.and().addFilterBefore(slackIdentitySsoFilter(), BasicAuthenticationFilter.class);
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
//		web.ignoring().antMatchers("/slack/**", "/app.bundle.js");
	}
	
	@Bean
	public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}
	
	private Filter slackIdentitySsoFilter() {
		CompositeFilter filter = new CompositeFilter();
		List<Filter> filters = new ArrayList<>();
		filters.add(ssoFilter(slackIdentity(), "/login/slack"));
		filter.setFilters(filters);
		return filter;
	}
	
	private Filter ssoFilter(ClientResource client, String path) {
		OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
		OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
		filter.setRestTemplate(template);
		UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(),
				client.getClient().getClientId());
		tokenServices.setRestTemplate(template);
		tokenServices.setPrincipalExtractor(new RidesharePrincipalExtractor());
		tokenServices.setAuthoritiesExtractor(new RideshareAuthoritiesExtractor());
		filter.setTokenServices(tokenServices);
		return filter;
	}
	
	@Bean
	@ConfigurationProperties("slack.identity")
	public ClientResource slackIdentity() {
		return new ClientResource();
	}
	
	@Bean
	@ConfigurationProperties("slack.integration")
	public ClientResource slackIntegration() {
		return new ClientResource();
	}
	
}
