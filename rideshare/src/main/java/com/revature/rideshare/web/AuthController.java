package com.revature.rideshare.web;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.exception.SlackApiException;
import com.revature.rideshare.security.RideshareAuthenticationToken;
import com.revature.rideshare.service.AuthService;
import com.revature.rideshare.service.UserService;

@RestController
@RequestMapping("auth")
public class AuthController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${slack.identity.client.clientId}")
	private String slackAppId;
	@Value("${slack.identity.client.clientSecret}")
	private String slackAppSecret;
	@Value("${slack.verificationToken}")
	private String slackAppVerificationToken;
	@Value("${slack.teamId}")
	private String slackAppTeamId;
	@Value("${deploy.url}")
	private String rideshareUrl;	

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
	
	@RequestMapping("/authorize")
	public ResponseEntity<String> redirectToSlack(@RequestParam(name="integrate", required=false) Boolean integrate, HttpServletResponse response) {
		ResponseEntity<String> res = null;
		HttpHeaders headers = new HttpHeaders();
		if (integrate != null) {
			String url = "https://slack.com/oauth/authorize?"
					+ "client_id=" + slackAppId
					+ "&team=" + slackAppTeamId
					+ "&scope=incoming-webhook,commands"
					+ "&redirect_uri=" + rideshareUrl + "/auth/integrate";
			headers.add("Location", url);
			res = new ResponseEntity<String>(headers, HttpStatus.SEE_OTHER);
//				response.sendRedirect(url);
		} else {
			String url = "https://slack.com/oauth/authorize?"
					+ "client_id=" + slackAppId
					+ "&team=" + slackAppTeamId
					+ "&scope=identity.basic,identity.email,identity.team"
					+ "&redirect_uri=" + rideshareUrl + "/auth/login";
			headers.add("Location", url);
			res = new ResponseEntity<String>(headers, HttpStatus.SEE_OTHER);
//				response.sendRedirect(url);
		}
		return res;
	}
	
	@RequestMapping("/login")
	public ResponseEntity<String> loginWithSlack(@RequestParam(name="code", required=false) String code,
			@RequestParam(name="error", required=false) String error) {
		System.out.println("got login request");
		String url = rideshareUrl + "/";
		HttpHeaders headers = new HttpHeaders();
		headers.add("WWW-Authenticate", "Bearer realm='Revature RideShare application'");
		headers.add("Location", url);
		String body = "{slack_error: " + error + "}";
		ResponseEntity<String> response = new ResponseEntity<String>(body, headers, HttpStatus.UNAUTHORIZED);
		if (code != null) {
			System.out.println("got authorization code");
			try {
				String slackToken = authService.getSlackAccessToken(code);
				System.out.println("got access token");
				String slackId = authService.getUserIdentity(slackToken);
				System.out.println("got user's slack id");
				JsonNode userInfo = authService.getUserInfo(slackToken, slackId);
				System.out.println("got user's information");
				User u = authService.getUserAccount(slackId, userInfo);
				System.out.println("got user account");
				String token = authService.createJsonWebToken(u);
				System.out.println("got json web token");
				RideshareAuthenticationToken auth = new RideshareAuthenticationToken(slackId, token, u, u.getAuthorities());
				System.out.println("got authentication");
				auth.setAuthenticated(true);
				SecurityContextHolder.getContext().setAuthentication(auth);
				HttpHeaders successHeaders = new HttpHeaders();
				successHeaders.add("Location", url);
				successHeaders.add("rideshare-token", token);
				response = new ResponseEntity<String>(successHeaders, HttpStatus.SEE_OTHER);
			} catch (SlackApiException ex) {
				logger.error("Slack API returned an error", ex);
			}
		}
		return response;
	}
	
	@RequestMapping("/integrate")
	public ResponseEntity<String> integrateWithSlack(Authentication authentication,
			@RequestParam(name="code", required=false) String code,
			@RequestParam(name="error", required=false) String error) {
		String url = rideshareUrl + "/";
		HttpHeaders headers = new HttpHeaders();
		headers.add("WWW-Authenticate", "Bearer realm='Revature RideShare application'");
		headers.add("Location", url);
		String body = "{slack_error: " + error + "}";
		ResponseEntity<String> response = new ResponseEntity<String>(body, headers, HttpStatus.UNAUTHORIZED);
		if (code != null) {
			System.out.println("got authorization code");
			try {
				JsonNode accessResponse = authService.getSlackAccessResponse(code);
				User u = authService.integrateUser(accessResponse);
				String token = authService.createJsonWebToken(u);
				HttpHeaders successHeaders = new HttpHeaders();
				successHeaders.add("Location", url);
				successHeaders.add("rideshare-token", token);
				response = new ResponseEntity<String>(successHeaders, HttpStatus.SEE_OTHER);
			} catch (SlackApiException ex) {
				logger.error("Slack API returned an error", ex);
			}
		}
		return response;
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

//	@GetMapping("/token")
//	public User getJsonWebToken(OAuth2Authentication authentication, HttpServletResponse response) {
//		String[] nameTokens = authentication.getName().split(", ");
//		String slackId = nameTokens[1].substring(3);
//		String token = authService.createJsonWebToken(slackId);
//		System.out.println(token);
//		response.addHeader("token", token);
//		return u;
//	}

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
