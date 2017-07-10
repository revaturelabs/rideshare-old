package com.revature.rideshare.web;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.UserService;

@RestController
public class AuthController {
	
	private String slackAppId = "184219023015.209820937091";
	private String slackAppSecret = "f69b998afcc9b1043adfa2ffdab49308";
	private String slackAppToken = "xER6r1Zrr0nxUBdSz7Fyq5UU";
	private String slackAppTeam = "";
	
	@Autowired
	UserService userService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@RequestMapping("/auth/check")
	public Boolean isAuthenticated(Principal principal) {
		return principal != null;
	}
	
	@RequestMapping("/auth/current")
	public User getCurrentUser(Principal principal) {
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(principal);
		System.out.println(principal.getName());
		String principalName = principal.getName();
		try {
			JsonNode root = mapper.readTree(principal.getName());
			String fullname = root.path("name").asText();
			String slackId = root.path("id").asText();
			User u = userService.getUserBySlackId(slackId);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping("auth/getCode")
	public void loginUser(@RequestParam("code") String code, HttpServletResponse response) {
		String destination = "/login?error=true";
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();
		String accessUrl = "https://slack.com/api/oauth.access?client_id=" + slackAppId
				+ "&client_secret=" + slackAppSecret
				+ "&code=" + code
				+ "&team=" + slackAppTeam;
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
		} catch (IOException e) {
			e.printStackTrace(); // TODO: change this when logging is set up
			try {
				response.sendRedirect(destination);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
}
