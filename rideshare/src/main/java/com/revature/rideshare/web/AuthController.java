package com.revature.rideshare.web;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.UserService;

@RestController
public class AuthController {
	
	@Autowired
	UserService userService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@RequestMapping("/auth/current")
	public User getCurrentUser(Principal principal) {
		System.out.println(principal);
		System.out.println(principal.getName());
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root;
		try {
			root = mapper.readTree(principal.getName());
			String fullname = root.path("name").asText();
			String slackId = root.path("id").asText();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
//	@RequestMapping("auth/getCode")
//	public void loginUser(@RequestParam("code") String code, HttpServletResponse response) {
//		String destination = "/login?error=true";
//		String url = "https://slack.com/api/oauth.access?client_id=" + slackAppId + "&client_secret=" + slackAppSecret + "&code=" + code;
//		ObjectMapper mapper = new ObjectMapper();
//		JsonNode root;
//		try {
//			root = mapper.readTree(accessResponse.getBody());
//			String accessToken = root.path("access_token").asText();
//			String tokenUrl = "https://slack.com/api/users.identity?token=" + accessToken;
//			RestTemplate requestTemplate = new RestTemplate();
//			ResponseEntity<String> tokenResponse = requestTemplate.getForEntity(tokenUrl, String.class);
//			System.out.println(tokenResponse);
//			ObjectMapper tokenMapper = new ObjectMapper();
//			JsonNode tokenRoot=tokenMapper.readTree(tokenResponse.getBody());
//			String userName = tokenRoot.path("user").path("name").asText();
//			String userId = tokenRoot.path("user").path("id").asText();
//			System.out.println("userName: " + userName + ", userId: " + userId);
//			User u = userService.getUserBySlackId(userId);
//			//TODO:update user information here
//			if(u==null){
//				u = new User();
//				u.setFullName(userName);
//				u.setSlackId(userId);
//				userService.addUser(u);
//			}
//			Authentication authentication = new PreAuthenticatedAuthenticationToken(u,
//					"blahblahblah"); // can include authorities as third parameter
//			SecurityContextHolder.getContext().setAuthentication(authentication);
//			destination = "/";
//			response.sendRedirect(destination);
//		} catch (IOException e) {
//			e.printStackTrace(); // TODO: change this when logging is set up
//			try {
//				response.sendRedirect(destination);
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//		}
//	}
	
}
