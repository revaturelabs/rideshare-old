package com.revature.rideshare.web;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.exception.BannedUserException;
import com.revature.rideshare.security.RideshareAuthenticationToken;
import com.revature.rideshare.service.AuthService;
import com.revature.rideshare.service.UserService;

@RestController
@RequestMapping("auth")
public class AuthController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
	
	@RequestMapping("/process")
	public ResponseEntity<String> processAuthentication(OAuth2Authentication authentication) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("WWW-Authenticate", "Bearer realm='Revature RideShare application'");
		ResponseEntity<String> response = new ResponseEntity<String>(headers, HttpStatus.UNAUTHORIZED);
		if (authentication != null) {
			String token = ((OAuth2AuthenticationDetails) authentication.getDetails()).getTokenValue();
			String slackId = authentication.getName().split(", ")[1].substring(3);
			JsonNode userInfo = authService.getUserInfo(token, slackId);
			try {
				User u = authService.getUserAccount(slackId, userInfo);
				String jwt = authService.createJsonWebToken(u);
				RideshareAuthenticationToken auth = new RideshareAuthenticationToken(slackId, jwt, u, u.getAuthorities());
				auth.setAuthenticated(true);
				SecurityContextHolder.getContext().setAuthentication(auth);
				HttpHeaders successHeaders = new HttpHeaders();
				successHeaders.add("rideshare-token", jwt);
				response = new ResponseEntity<String>(successHeaders, HttpStatus.OK);
			} catch (BannedUserException ex) {
				HttpHeaders bannedHeaders = new HttpHeaders();
				String redirect = "/#/error?reason=ban";
				bannedHeaders.add("Location", redirect);
				String bannedBody = "Your account has been banned from this application";
				response = new ResponseEntity<String>(bannedBody, bannedHeaders, HttpStatus.SEE_OTHER);
			}
		}
		return response;
	}
	
//	@RequestMapping("/authorize")
//	public ResponseEntity<String> redirectToSlack(@RequestParam(name="integrate", required=false) Boolean integrate, HttpServletResponse response) {
//		ResponseEntity<String> res = null;
//		HttpHeaders headers = new HttpHeaders();
//		if (integrate != null) {
//			String url = "https://slack.com/oauth/authorize?"
//					+ "client_id=" + slackAppId
//					+ "&team=" + slackAppTeamId
//					+ "&scope=incoming-webhook,commands"
//					+ "&redirect_uri=" + rideshareUrl + "/auth/integrate";
//			headers.add("Location", url);
//			res = new ResponseEntity<String>(headers, HttpStatus.SEE_OTHER);
//		} else {
//			String url = "https://slack.com/oauth/authorize?"
//					+ "client_id=" + slackAppId
//					+ "&team=" + slackAppTeamId
//					+ "&scope=identity.basic,identity.email,identity.team"
//					+ "&redirect_uri=" + rideshareUrl + "/auth/login";
//			headers.add("Location", url);
//			res = new ResponseEntity<String>(headers, HttpStatus.SEE_OTHER);
//		}
//		return res;
//	}
	
	// TODO: update this method to be used after the user has logged in
//	@RequestMapping("/integrate")
//	public ResponseEntity<String> integrateWithSlack(OAuth2Authentication authentication) {
//		String url = rideshareUrl + "/#/error?code=418";
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("WWW-Authenticate", "Bearer realm='Revature RideShare application'");
//		headers.add("Location", rideshareUrl);
//		String body = "{slack_error: " + error + "}";
//		ResponseEntity<String> response = new ResponseEntity<String>(body, headers, HttpStatus.SEE_OTHER);
//		if (code != null) {
//			System.out.println("got authorization code");
//			try {
//				JsonNode accessResponse = authService.getSlackAccessResponse(code);
//				User u = authService.integrateUser(accessResponse);
//				String token = authService.createJsonWebToken(u);
//				HttpHeaders successHeaders = new HttpHeaders();
//				successHeaders.add("Location", url);
//				successHeaders.add("rideshare-token", token);
//				response = new ResponseEntity<String>(successHeaders, HttpStatus.SEE_OTHER);
//			} catch (SlackApiException ex) {
//				logger.error("Slack API returned an error", ex);
//			}
//		}
//		return response;
//	}
	
}
