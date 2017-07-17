package com.revature.rideshare;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * 
 * @author Eric Christie
 * @created July 9, 2017
 */
@Configuration
//@EnableWebSecurity
//@EnableOAuth2Sso
@EnableOAuth2Client
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

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
				.antMatchers("/login**", "/app.bundle.js", "/css", "/images", "/partials/slackLogin.html", "/auth/check").permitAll()
				.anyRequest().authenticated()
			.and().logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/")
				.clearAuthentication(true)
				.invalidateHttpSession(true)
				.permitAll()
			.and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			.and().addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css", "/images");
	}

	private Filter ssoFilter() {
		OAuth2ClientAuthenticationProcessingFilter slackFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/slack");
		OAuth2RestTemplate slackTemplate = new OAuth2RestTemplate(slack(), oauth2ClientContext);
		slackFilter.setRestTemplate(slackTemplate);
		UserInfoTokenServices tokenServices = new UserInfoTokenServices(slackIdentityResource().getUserInfoUri(), slack().getClientId());
		tokenServices.setRestTemplate(slackTemplate);
		slackFilter.setTokenServices(tokenServices);
		return slackFilter;
	}
	@Bean
	@ConfigurationProperties("slack.client")
	public AuthorizationCodeResourceDetails slack() {
		return new AuthorizationCodeResourceDetails();
	}
	@Bean
	@ConfigurationProperties("slack.resource.identity")
	public ResourceServerProperties slackIdentityResource() {
		return new ResourceServerProperties();
	}
	@Bean
	@ConfigurationProperties("slack.resource.profile")
	public ResourceServerProperties slackProfileResource() {
		return new ResourceServerProperties();
	}
	@Bean
	public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}
}