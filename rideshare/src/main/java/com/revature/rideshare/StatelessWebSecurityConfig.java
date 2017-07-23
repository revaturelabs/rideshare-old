//package com.revature.rideshare;
//
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//
///**
// * A stateless web security configuration that uses token-based authentication
// * @author Eric Christie
// * @created July 13, 2017
// */
////@Configuration
//public class StatelessWebSecurityConfig extends WebSecurityConfigurerAdapter {
//	
////	@Override
////	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
////		auth.authenticationProvider(authenticationProvider)
////	}
//	
////	private Filter ssoFilter() {
////	OAuth2ClientAuthenticationProcessingFilter slackFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/slack");
////	OAuth2RestTemplate slackTemplate = new OAuth2RestTemplate(slack(), oauth2ClientContext);
////	slackFilter.setRestTemplate(slackTemplate);
////	UserInfoTokenServices tokenServices = new UserInfoTokenServices(slackIdentityResource().getUserInfoUri(), slack().getClientId());
////	tokenServices.setRestTemplate(slackTemplate);
////	slackFilter.setTokenServices(tokenServices);
////	return slackFilter;
////}
////@Bean
////@ConfigurationProperties("slack.identity.client")
////public AuthorizationCodeResourceDetails slack() {
////	return new AuthorizationCodeResourceDetails();
////}
////@Bean
////@ConfigurationProperties("slack.identity.resource")
////public ResourceServerProperties slackIdentityResource() {
////	return new ResourceServerProperties();
////}
//	
////	class RideshareAuthenticationManager implements AuthenticationManager {
////	@Override
////	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
////		return null;
////	}
////}
//
//}
