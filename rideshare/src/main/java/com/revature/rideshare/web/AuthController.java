package com.revature.rideshare.web;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.UserService;

@Controller
public class AuthController {
//	@Value("#{systemEnvironment['TESTAPP_ID']}")
	private static String slackAppId = "184219023015.209820937091";
//	@Value("#{systemEnvironment['TESTAPP_SECRET']}")
	private static String slackAppSecret = "f69b998afcc9b1043adfa2ffdab49308";
//	@Value("#{systemEnvironment['TESTAPP_TEAM']}")
//	private static String slackAppToken = "xER6r1Zrr0nxUBdSz7Fyq5UU";
//	@Value("#{systemEnvironment['TESTAPP_TEAM']}")
//	private static String slackTeamId;
	
	@Autowired
	UserService userService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@RequestMapping("auth/getCode") //@GetMapping("/code")
	public String loginUser(@RequestParam("code") String code) {
		String url = "https://slack.com/api/oauth.access?client_id=" + slackAppId
				+ "&client_secret=" + slackAppSecret 
				+ "&code=" + code;
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		System.out.println(response);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root;
		try {
			root = mapper.readTree(response.getBody());
			Boolean isOk = root.path("ok").asBoolean();
			System.out.println(isOk);
			String accessToken = root.path("access_token").asText();
//			String[] scopes = root.path("scope").as;
//			String teamId = root.path("team_id").asText();
			String tokenUrl = "https://slack.com/api/users.identity?token=" + accessToken;
			RestTemplate requestTemplate = new RestTemplate();
			ResponseEntity<String> tokenResponse = requestTemplate.getForEntity(tokenUrl, String.class);
			System.out.println(tokenResponse);
			ObjectMapper tokenMapper = new ObjectMapper();
			JsonNode tokenRoot=tokenMapper.readTree(tokenResponse.getBody());
			String userName = tokenRoot.path("user").path("name").asText();
			String userId = tokenRoot.path("user").path("id").asText();
			System.out.println("userName: " + userName + ", userId: " + userId);
			User u = userService.getUserBySlackId(userId);
			//TODO:update user information
			if(u==null){
				u = new User();
				u.setFullName(userName);
				u.setSlackId(userId);
				userService.addUser(u);
			}
			Authentication authentication = new PreAuthenticatedAuthenticationToken(u,
					"blahblahblah"); // can include authorities as third parameter
			SecurityContextHolder.getContext().setAuthentication(authentication);
			return "redirect:/";
		} catch (IOException e) {
			e.printStackTrace(); // TODO: change this when logging is set up
			return "redirect:/login?error=true";
		}
	}
	
	@GetMapping("/currentUser")
	public User getCurrentUser(Authentication authentication) {
		authentication = (PreAuthenticatedAuthenticationToken) authentication;
		return (User) authentication.getPrincipal();
	}
	
}
