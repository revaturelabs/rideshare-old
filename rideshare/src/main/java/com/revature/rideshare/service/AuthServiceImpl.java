package com.revature.rideshare.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.rideshare.domain.User;

//@Component
public class AuthServiceImpl {
	
	// TODO: start using an environment variable when this application is
	// deployed
	// @Value("#{systemEnvironment['RIDESHARE_JWT_SECRET']}")
	private String jwtSecret = "Richie is obsessed with chickens!";
	
	private String slackAppId = "184219023015.209820937091";
	private String slackAppSecret = "f69b998afcc9b1043adfa2ffdab49308";
	private String slackAppVerificationToken = "xER6r1Zrr0nxUBdSz7Fyq5UU";
	private String slackAppTeamId = "T5E6F0P0F"; // for 1705may15java
	
	@Autowired
	OAuth2ClientContext oauth2ClientContext;
	
	@Autowired
	AuthorizationCodeResourceDetails slackResource;
	
	@Autowired
	UserService userService;
	
	@Autowired
	PointOfInterestService poiService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public void setPoiService(PointOfInterestService poiService) {
		this.poiService = poiService;
	}

	public void setSlackResource(AuthorizationCodeResourceDetails slackResource) {
		this.slackResource = slackResource;
	}
	
	@Bean
	public OAuth2RestTemplate slackTemplate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context) {
		return new OAuth2RestTemplate(resource, context);
	}
	
	public String getSlackAccessToken(String code) {
		RestTemplate client = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();
		String result = null;
		String url = "https://slack.com/api/oauth.access";
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
		requestBody.add("client_id", slackAppId);
		requestBody.add("client_secret", slackAppSecret);
		requestBody.add("code", code);
		ResponseEntity<String> response = client.postForEntity(url, requestBody, String.class);
		try {
			JsonNode body = mapper.readTree(response.getBody());
			if (body.path("ok").asBoolean()) {
				result = body.path("access_token").asText();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	public JsonNode getSlackAccessResponse(String code) {
		RestTemplate client = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode result = null;
		String url = "https://slack.com/api/oauth.access";
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
		requestBody.add("client_id", slackAppId);
		requestBody.add("client_secret", slackAppSecret);
		requestBody.add("code", code);
		ResponseEntity<String> response = client.postForEntity(url, requestBody, String.class);
		try {
			result = mapper.readTree(response.getBody());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Gets the slack id for the authenticated user
	 * @param token The access token obtained from slack
	 */
	public String getUserIdentity(String token) {
		RestTemplate client = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();
		String result = null;
		String url = "https://slack.com/api/users.identity";
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
		requestBody.add("token", token);
		ResponseEntity<String> response = client.postForEntity(url, requestBody, String.class);
		try {
			JsonNode body = mapper.readTree(response.getBody());
			if (body.path("ok").asBoolean()) {
				result = body.path("user").path("id").asText();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	public JsonNode getUserProfile(String token, String slackId) {
		RestTemplate client = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode result = null;
		String url = "https://slack.com/api/users.profile.get";
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
		requestBody.add("token", token);
		requestBody.add("user", slackId);
		ResponseEntity<String> response = client.postForEntity(url, requestBody, String.class);
		try {
			JsonNode body = mapper.readTree(response.getBody());
			if (body.path("ok").asBoolean()) {
				result = body.path("profile");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	public JsonNode getUserInfo(String token, String slackId) {
		RestTemplate client = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode result = null;
		String url = "https://slack.com/api/users.info";
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
		requestBody.add("token", token);
		requestBody.add("user", slackId);
		ResponseEntity<String> response = client.postForEntity(url, requestBody, String.class);
		try {
			JsonNode body = mapper.readTree(response.getBody());
			if (body.path("ok").asBoolean()) {
				result = body.path("user");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	public User getAuthenticatedUser(String code) {
		User result = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode accessResponse = getSlackAccessResponse(code);
			if (accessResponse.path("ok").asBoolean()) {
				String slackId = accessResponse.path("user_id").asText();
				String teamId = accessResponse.path("team_id").asText();
				String token = accessResponse.path("access_token").asText();
				JsonNode incomingWebhook = mapper.readTree(accessResponse.path("incoming_webhook").asText());
				JsonNode userInfo = getUserInfo(token, slackId);
				User u = userService.getUserBySlackId(slackId);
				if (u == null) {
					u = new User();
					u.setSlackId(slackId);
					u.setAdmin(false);
					u.setEmail(userInfo.path("profile").path("email").asText());
					u.setFirstName(userInfo.path("profile").path("first_name").asText());
					u.setLastName(userInfo.path("profile").path("last_name").asText());
					u.setFullName(userInfo.path("real_name").asText());
					u.setMainPOI(poiService.getOnePoiByName("Icon at Dulles"));
					u.setWorkPOI(poiService.getOnePoiByName("Revature Office"));
					userService.addUser(u);
				} else {
					u.setEmail(userInfo.path("profile").path("email").asText());
					u.setFirstName(userInfo.path("profile").path("first_name").asText());
					u.setLastName(userInfo.path("profile").path("last_name").asText());
					u.setFullName(userInfo.path("real_name").asText());
					userService.updateUser(u);
				}
				result = u;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	public String createJsonWebToken(User u) {
		String jwt = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			Algorithm alg = Algorithm.HMAC256(jwtSecret);
			String userJson = mapper.writeValueAsString(u);
			jwt = JWT.create()
					.withIssuer("Revature RideShare")
					.withIssuedAt(new Date())
					.withExpiresAt(new Date())
					.withAudience("Revature RideShare AngularJS Client")
					.withClaim("user", userJson)
					.withClaim("admin", u.isAdmin())
					.sign(alg);
		} catch (IllegalArgumentException | UnsupportedEncodingException | JsonProcessingException ex) {
			ex.printStackTrace();
		}
		return jwt;
	}

}
