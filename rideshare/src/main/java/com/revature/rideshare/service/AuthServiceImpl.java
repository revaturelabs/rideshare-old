//package com.revature.rideshare.service;
//
//import java.io.UnsupportedEncodingException;
//import java.util.Date;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.oauth2.client.OAuth2ClientContext;
//import org.springframework.security.oauth2.client.OAuth2RestTemplate;
//import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
//import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.revature.rideshare.domain.User;
//
////@Component
//public class AuthServiceImpl implements AuthService {
//	
//	// TODO: start using an environment variable when this application is
//	// deployed
//	// @Value("#{systemEnvironment['RIDESHARE_JWT_SECRET']}")
//	private String jwtSecret = "Richie is obsessed with chickens!";
//	
//	private String slackAppId = "184219023015.209820937091";
//	private String slackAppSecret = "f69b998afcc9b1043adfa2ffdab49308";
//	private String slackAppToken = "xER6r1Zrr0nxUBdSz7Fyq5UU";
//	private String slackAppTeamId = "T5E6F0P0F"; // for 1705may15java
//	
//	@Autowired
//	OAuth2ClientContext oauth2ClientContext;
//	
//	@Autowired
//	UserService userService;
//
//	public void setUserService(UserService userService) {
//		this.userService = userService;
//	}
//	
//	@Bean
//	@ConfigurationProperties("slack.client")
//	public AuthorizationCodeResourceDetails slackResource() {
//		return new AuthorizationCodeResourceDetails();
//	}
//	
//	@Bean
//	public OAuth2RestTemplate slackTemplate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context) {
//		return new OAuth2RestTemplate(resource, context);
//	}
//	
//	public String getSlackAccessToken() {
//		
//	}
//	
//	public String getUserIdentity() {
//		
//	}
//	
//	
//	
//	public String createJsonWebToken(String slackId) {
//		try {
//			ObjectMapper mapper = new ObjectMapper();
//			Algorithm alg = Algorithm.HMAC256(jwtSecret);
//			User u = userService.getUserBySlackId(slackId);
//			// TODO: get user's profile from slack
//			if (u == null) {
//				u = new User();
//				u.setSlackId(slackId);
//				userService.addUser(u);
//			}
//			String userJson;
//			userJson = mapper.writeValueAsString(u);
//			String token = JWT.create()
//					.withIssuer("Revature RideShare")
//					.withIssuedAt(new Date())
//					.withAudience("Revature RideShare AngularJS Client")
//					.withClaim("user", userJson).sign(alg);
//		}  catch (IllegalArgumentException | UnsupportedEncodingException | JsonProcessingException ex) {
//			ex.printStackTrace();
//			return null;
//		}
//	}
//	
//	
//	
//	public User getAuthenticatedUser() {
//		return null;
//	}
//
//}
