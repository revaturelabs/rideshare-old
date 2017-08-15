package com.revature.rideshare.security;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.AuthService;

@Component
public class RideshareAuthoritiesExtractor implements AuthoritiesExtractor {
	
	@Autowired
	private AuthService authService;

	public void setAuthService(AuthService authService) {
		this.authService = authService;
	}

	public RideshareAuthoritiesExtractor() { super(); }

	/*
	 * Banned/disabled users will have no authorities.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		if ((Boolean) map.get("ok")) {
			for (Entry<String, Object> e: map.entrySet()) {
				System.out.println(e.getKey() + " = " + e.getValue());
			}
			LinkedHashMap<String, String> user = (LinkedHashMap<String, String>) map.get("user");
			String fullname = user.get("name");
			String slackId = user.get("id");
			String email = user.get("email");
			User u = authService.getUserAccount(fullname, slackId, email);
			if (u.isEnabled()) {
				authorities.addAll(u.getAuthorities());
			}
		}
		return authorities;
	}
	
}
