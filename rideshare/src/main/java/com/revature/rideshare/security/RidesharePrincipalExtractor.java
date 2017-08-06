package com.revature.rideshare.security;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

public class RidesharePrincipalExtractor implements PrincipalExtractor {
	
	/*
	 * A principal will identify a user by Slack Id.
	 * Email could also be used to identify users if the Slack integration needs to work with multiple teams.
	 */
	@Override
	public Object extractPrincipal(Map<String, Object> map) {
		String principal = null;
		System.out.println("in principal extractor");
		if (new Boolean((String) map.get("ok"))) {
			for (Entry<String, Object> e: map.entrySet()) {
				System.out.println(e.getKey() + " = " + e.getValue());
			}
			String[] parts = ((String) map.get("user")).split(", ");
			String fullname = parts[0].substring(6);
			String slackId = parts[1].substring(3);
			String email = parts[2].substring(6, parts[2].length() - 1);
			System.out.println("name = " + fullname + ", slackId = " + slackId + ", email = " + email);
			principal = slackId;
		}
		return principal;
	}

}
