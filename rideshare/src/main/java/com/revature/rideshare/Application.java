//package com.revature.rideshare;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
//import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan.Filter;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.oauth2.client.OAuth2ClientContext;
//import org.springframework.security.oauth2.client.OAuth2RestTemplate;
//import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
//import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
//import org.springframework.web.bind.annotation.RestController;
//
//@SpringBootApplication
//@EnableOAuth2Client
//@RestController
//public class Application3 extends WebSecurityConfigurerAdapter {
//	
//	@Autowired
//	OAuth2ClientContext oauth2ClientContext;
//	
//	private Filter ssoFilter() {
//	    OAuth2ClientAuthenticationProcessingFilter slackFilter = new OAuth2ClientAuthenticationProcessingFilter(
//	            "/login/slack");
//	    OAuth2RestTemplate slackTemplate = new OAuth2RestTemplate(slack(), oauth2ClientContext);
//	    slackFilter.setRestTemplate(slackTemplate);
//	    UserInfoTokenServices tokenServices = new UserInfoTokenServices(slackResource().getUserInfoUri(),
//	            slack().getClientId());
//	    tokenServices.setRestTemplate(slackTemplate);
//	    slackFilter.setTokenServices(
//	            new UserInfoTokenServices(slackResource().getUserInfoUri(), slack().getClientId()));
//	    return (Filter) slackFilter;
//	}
//
//	@Bean
//	@ConfigurationProperties("slack.client")
//	public AuthorizationCodeResourceDetails slack() {
//	    return new AuthorizationCodeResourceDetails();
//	}
//
//	@Bean
//	@ConfigurationProperties("slack.resource")
//	public ResourceServerProperties slackResource() {
//	    return new ResourceServerProperties();
//	}
//
//}
