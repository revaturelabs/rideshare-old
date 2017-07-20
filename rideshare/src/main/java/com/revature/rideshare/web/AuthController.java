package com.revature.rideshare.web;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.AuthService;
import com.revature.rideshare.service.UserService;

//@RestController
//@RequestMapping("auth")
public class AuthController {

	@Value("${slack.identity.client.clientId}")
	private String slackAppId;
	@Value("${slack.identity.client.clientSecret}")
	private String slackAppSecret;
	@Value("${slack.verificationToken}")
	private String slackAppVerificationToken;
	@Value("${slack.teamId}")
	private String slackAppTeamId;

	@Autowired
	UserService userService;
	
	@Autowired
	AuthService authService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public void setAuthService(AuthService authService) {
		this.authService = authService;
	}

	@RequestMapping("/check")
	public Boolean isAuthenticated(Principal principal) {
		return principal != null;
	}

	@RequestMapping("/test")
	public void testAuthentication(OAuth2Authentication authentication, HttpServletRequest request) {
//		for (Enumeration<String> headers = request.getHeaderNames(); headers.hasMoreElements();) {
//			String name = headers.nextElement();
//			System.out.println(name + ": " + request.getHeader(name));
//		}
		
	}
	
//	@RequestMapping("/slack/authorize")
//	public ResponseEntity<String> redirectToSlack(HttpServletResponse response) {
//		
//	}
	
	@RequestMapping("/slack/login")
	public ResponseEntity<String> loginWithSlack(@RequestParam(name="code", required=false) String code,
			@RequestParam(name="error", required=false) String error) {
		return null;
	}
	
	@RequestMapping("/slack/integrate")
	public ResponseEntity<String> integrateWithSlack(@RequestParam(name="code", required=false) String code,
			@RequestParam(name="error", required=false) String error) {
		return null;
	}

	/*
	 * TODO: this method is currently a hackish quick fix, find a better solution
	 * NOTE: slack user IDs are only unique within a specific team, but
	 * team IDs are unique across all of slack
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
		String[] nameTokens = authentication.getName().split(", ");
//		String fullName = nameTokens[0].substring(6);
		String slackId = nameTokens[1].substring(3);
//		String email = nameTokens[2].substring(6, nameTokens[2].length() - 1);
		String token = authService.createJsonWebToken(slackId);
		System.out.println(token);
		response.addHeader("token", token);
		return u;
	}

	@RequestMapping("/getCode")
	public void loginUser(@RequestParam("code") String code, HttpServletResponse response) {
		String destination = "/login?error=true";
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();
		String accessUrl = "https://slack.com/api/oauth.access?client_id=" + slackAppId + "&client_secret="
				+ slackAppSecret + "&code=" + code;
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
			// TODO:update user information here
			if (u == null) {
				u = new User();
				u.setFullName(userName);
				u.setSlackId(userId);
				userService.addUser(u);
			}
			Authentication authentication = new PreAuthenticatedAuthenticationToken(u, "blahblahblah");
			// can include authorities as third parameter
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
