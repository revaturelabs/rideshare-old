package com.revature.rideshare.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.UserService;

@RestController
@RequestMapping("auth")
public class AuthController {
	
	// TODO: start using an environment variable when this application is deployed
//	@Value("#{systemEnvironment['RIDESHARE_JWT_SECRET']}")
	private String jwtSecret = "Richie is obsessed with chickens!";
	
	private String slackAppId = "184219023015.209820937091";
	private String slackAppSecret = "f69b998afcc9b1043adfa2ffdab49308";
	private String slackAppToken = "xER6r1Zrr0nxUBdSz7Fyq5UU";
	private String slackAppTeamId = "T5E6F0P0F"; // for 1705may15java
	
	@Autowired
	UserService userService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@RequestMapping("/check")
	public Boolean isAuthenticated(Principal principal) {
		return principal != null;
	}
	
	@RequestMapping("/test")
	public void testAuthentication(OAuth2Authentication authentication, HttpServletRequest request) {
		for (Enumeration<String> headers = request.getHeaderNames(); headers.hasMoreElements();) {
			String name = headers.nextElement();
			System.out.println(name + ": " + request.getHeader(name));
		}
	}
	
	/*
	 * TODO: this method is currently a hackish quick fix, find a better solution
	 * NOTE: slack user IDs are only unique within a specific team, but team IDs are unique across all of slack
	 */
	@RequestMapping("/current")
	public User getCurrentUser(OAuth2Authentication authentication, HttpServletRequest request) {	
		String[] nameTokens = authentication.getName().split(", ");
		String fullName = nameTokens[0].substring(6);
		System.out.println(fullName);
		String slackId = nameTokens[1].substring(3);
		System.out.println(slackId);
		String email = nameTokens[2].substring(6, nameTokens[2].length() - 1);
		System.out.println(email);
		User u = userService.getUserBySlackId(slackId);
		if (u == null) {
			System.out.println("creating new user");
			u = new User();
			u.setSlackId(slackId);
			u.setFullName(fullName);
			u.setEmail(email);
			u.setAdmin(false);
			userService.addUser(u);
		}
		return u;
	}
	
	@GetMapping("/token")
	public User getJsonWebToken(OAuth2Authentication authentication, HttpServletResponse response) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Algorithm alg = Algorithm.HMAC256(jwtSecret);
			String[] nameTokens = authentication.getName().split(", ");
			String fullName = nameTokens[0].substring(6);
			String slackId = nameTokens[1].substring(3);
			String email = nameTokens[2].substring(6, nameTokens[2].length() - 1);
			User u = userService.getUserBySlackId(slackId);
			if (u == null) {
				u = new User();
				u.setSlackId(slackId);
				u.setFullName(fullName);
				u.setEmail(email);
				u.setAdmin(false);
				userService.addUser(u);
			}
			String userJson;
			userJson = mapper.writeValueAsString(u);
			String token = JWT.create()
					.withIssuer("Revature RideShare")
					.withIssuedAt(new Date())
					.withAudience("Revature RideShare AngularJS Client")
					.withClaim("user", userJson)
					.sign(alg);
			System.out.println(token);
			response.addHeader("token", token);
			return u;
		} catch (IllegalArgumentException | UnsupportedEncodingException | JsonProcessingException ex) {
			ex.printStackTrace();
			response.addHeader("token", null);
			return null;
		}
	}
	
	@RequestMapping("/getCode")
	public void loginUser(@RequestParam("code") String code, HttpServletResponse response) {
		String destination = "/login?error=true";
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();
		String accessUrl = "https://slack.com/api/oauth.access?client_id=" + slackAppId
				+ "&client_secret=" + slackAppSecret
				+ "&code=" + code;
		ResponseEntity<String> accessResponse = restTemplate.getForEntity(accessUrl, String.class);
		try {
			JsonNode root = mapper.readTree(accessResponse.getBody());
			String accessToken = root.path("access_token").asText();
			String tokenUrl = "https://slack.com/api/users.identity?token=" + accessToken;
			RestTemplate requestTemplate = new RestTemplate();
			ResponseEntity<String> tokenResponse = requestTemplate.getForEntity(tokenUrl, String.class);
			System.out.println(tokenResponse);
			JsonNode tokenRoot = mapper.readTree(tokenResponse.getBody());
			String userName = tokenRoot.path("user").path("name").asText();
			String userId = tokenRoot.path("user").path("id").asText();
			System.out.println("userName: " + userName + ", userId: " + userId);
			User u = userService.getUserBySlackId(userId);
			//TODO:update user information here
			if(u==null){
				u = new User();
				u.setFullName(userName);
				u.setSlackId(userId);
				userService.addUser(u);
			}
			Authentication authentication = new PreAuthenticatedAuthenticationToken(u,
					"blahblahblah"); // can include authorities as third parameter
			SecurityContextHolder.getContext().setAuthentication(authentication);
			destination = "/";
			response.sendRedirect(destination);
		} catch (IOException ex) {
			ex.printStackTrace(); // TODO: change this when logging is set up
		}
	}
	
}
