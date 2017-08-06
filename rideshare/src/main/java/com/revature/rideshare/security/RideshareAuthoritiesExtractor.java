package com.revature.rideshare.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.security.core.GrantedAuthority;

import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.AuthService;

public class RideshareAuthoritiesExtractor implements AuthoritiesExtractor {
	
	@Autowired
	private AuthService authService;

	public void setAuthService(AuthService authService) {
		this.authService = authService;
	}

	/*
	 * Banned/disabled users will have no authorities.
	 */
	@Override
	public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		if (new Boolean((String) map.get("ok"))) {
			String[] parts = ((String) map.get("user")).split(", ");
			String fullname = parts[0].substring(6);
			String slackId = parts[1].substring(3);
			String email = parts[2].substring(6, parts[2].length() - 1);
			User u = authService.getUserAccount(fullname, slackId, email);
			if (u.isEnabled()) {
				authorities.addAll(u.getAuthorities());
			}
		}
		return authorities;
	}
	
}
