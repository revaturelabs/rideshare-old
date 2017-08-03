package com.revature.rideshare.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.exception.BannedUserException;
import com.revature.rideshare.exception.SlackApiException;
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
	public Boolean isAuthenticated(Authentication authentication) {
		return (authentication != null && authentication.isAuthenticated());
	}
	
	@RequestMapping("/process")
	public ResponseEntity<String> processAuthentication(OAuth2Authentication authentication) {
		System.out.println("processing authentication");
		HttpHeaders headers = new HttpHeaders();
		headers.add("WWW-Authenticate", "Bearer realm='Revature RideShare application'");
		ResponseEntity<String> response = new ResponseEntity<String>(headers, HttpStatus.UNAUTHORIZED);
		if (authentication != null) {
			System.out.println("authentication: " + authentication);
			String token = ((OAuth2AuthenticationDetails) authentication.getDetails()).getTokenValue();
			String[] parts = authentication.getName().split(", ");
			String slackId = parts[1].substring(3);
			JsonNode userIdentity = authService.getUserIdentity(token);
			JsonNode userInfo = null;
			try {
				userInfo = authService.getUserInfo(token, slackId);
			} catch (SlackApiException ex) {
				logger.error("Could not obtain complete slack account information for user", ex);
			}
			try {
				User u = authService.getUserAccount(userIdentity, userInfo);
				String jwt = authService.createJsonWebToken(u);
				RideshareAuthenticationToken auth = new RideshareAuthenticationToken(slackId, jwt, u, u.getAuthorities());
				auth.setAuthenticated(true);
				SecurityContextHolder.getContext().setAuthentication(auth);
				HttpHeaders successHeaders = new HttpHeaders();
				successHeaders.add("rideshare-identity-token", jwt);
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
	
}
