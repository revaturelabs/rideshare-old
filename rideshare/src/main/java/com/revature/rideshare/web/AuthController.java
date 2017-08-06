package com.revature.rideshare.web;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.AuthService;
import com.revature.rideshare.service.UserService;

@RestController
@RequestMapping("auth")
public class AuthController {
	
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

	@GetMapping("/check")
	public Boolean isAuthenticated(Authentication authentication) {
		return (authentication != null && authentication.isAuthenticated());
	}
	
	@GetMapping("/test")
	public ResponseEntity<Principal> testAuthentication(Authentication authentication, Principal principal) {
		ResponseEntity<Principal> response = null;
		System.out.println("authentication: " + authentication);
		System.out.println("principal: " + principal);
		return response;
	}
	
	@GetMapping("/identity")
	public ResponseEntity<String> getIdentityToken(Authentication authentication, Principal principal) {
		HttpHeaders headers = new HttpHeaders();
		User u = userService.getUserBySlackId(principal.getName());
		String jwt = authService.createJsonWebToken(u);
		headers.add("rideshare-identity-token", jwt);
		return new ResponseEntity<String>(jwt, headers, HttpStatus.OK);
	}
	
}
