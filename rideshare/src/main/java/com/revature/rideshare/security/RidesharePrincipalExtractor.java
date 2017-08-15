package com.revature.rideshare.security;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

public class RidesharePrincipalExtractor implements PrincipalExtractor {

	/*
	 * A principal will identify a user by Slack Id.
	 * Email could also be used to identify users if the Slack integration needs to work with multiple teams.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object extractPrincipal(Map<String, Object> map) {
		String principal = null;
		if ((Boolean) map.get("ok")) {
			LinkedHashMap<String, String> user = (LinkedHashMap<String, String>) map.get("user");
			String slackId = user.get("id");
//			String email = user.get("email");
			principal = slackId;
		}
		return principal;
	}

}
