package com.revature.rideshare.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.stereotype.Component;
import org.springframework.web.util.CookieGenerator;

import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.AuthService;
import com.revature.rideshare.service.UserService;

@Component
public class RideshareIdentityHeaderWriter implements HeaderWriter {
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private UserService userService;

	public void setAuthService(AuthService authService) {
		this.authService = authService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
		String jwt = null;
		if (request.getUserPrincipal() != null) {
			String slackId = request.getUserPrincipal().getName();
			User u = userService.getUserBySlackId(slackId);
			jwt = authService.createJsonWebToken(u);
		}
//		generator(request.getRemoteHost()).addCookie(response, jwt);
		response.addHeader("Set-RideshareIdentityToken", jwt);
	}
	
	private CookieGenerator generator(String domain) {
		CookieGenerator generator = new CookieGenerator();
		generator.setCookieName("RideshareIdentityToken");
		generator.setCookieDomain(domain);
		generator.setCookiePath("/");
//		generator.setCookieMaxAge(3600); // this allows the cookie to be retained after the session ends
		return generator;
	}

}
