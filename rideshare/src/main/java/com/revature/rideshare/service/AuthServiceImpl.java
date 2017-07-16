package com.revature.rideshare.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.rideshare.dao.PointOfInterestRepository;
import com.revature.rideshare.dao.UserRepository;
import com.revature.rideshare.domain.User;

//@Component
public class AuthServiceImpl implements AuthService {
	
//	TODO: start using environment variables when this application is deployed
//	@Value("#{systemEnvironment['RIDESHARE_JWT_SECRET']}")
	private String jwtSecret = "Richie is obsessed with chickens!";
//	@Value("#{systemEnvironment['RIDESHARE_SLACK_ID']}")
	private String slackAppId = "184219023015.209820937091";
//	@Value("#{systemEnvironment['RIDESHARE_SLACK_SECRET']}")
	private String slackAppSecret = "f69b998afcc9b1043adfa2ffdab49308";
//	@Value("#{systemEnvironment['RIDESHARE_SLACK_VERIFICATION']}")
	private String slackAppVerificationToken = "xER6r1Zrr0nxUBdSz7Fyq5UU";
//	@Value("#{systemEnvironment['RIDESHARE_SLACK_TEAM']}")
	private String slackAppTeamId = "T5E6F0P0F"; // for 1705may15java
	
//	@Autowired
//	OAuth2ClientContext oauth2ClientContext;
	
//	@Autowired
//	AuthorizationCodeResourceDetails slackResource;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	PointOfInterestRepository poiRepo;
	
	public AuthServiceImpl() {
		super();
	}

	@Override
	public void setUserRepo(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public void setPoiRepo(PointOfInterestRepository poiRepo) {
		this.poiRepo = poiRepo;
	}

//	@Override
//	public void setSlackResource(AuthorizationCodeResourceDetails slackResource) {
//		this.slackResource = slackResource;
//	}
	
//	@Override
//	@Bean
//	public OAuth2RestTemplate slackTemplate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context) {
//		return new OAuth2RestTemplate(resource, context);
//	}
	
	@Override
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
	
	@Override
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
	
	@Override
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
	
	@Override
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
	
	@Override
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
	
	@Override
	public User getAuthenticatedUser(String code) {
		User result = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode accessResponse = getSlackAccessResponse(code);
			if (accessResponse.path("ok").asBoolean()) {
				String slackId = accessResponse.path("user_id").asText();
//				String teamId = accessResponse.path("team_id").asText();
				String token = accessResponse.path("access_token").asText();
				JsonNode incomingWebhook = mapper.readTree(accessResponse.path("incoming_webhook").asText());
				String webhookUrl = incomingWebhook.path("url").asText();
				JsonNode userInfo = getUserInfo(token, slackId);
				User u = userRepo.findBySlackId(slackId);
				if (u == null) {
					u = new User();
					u.setSlackId(slackId);
					u.setAdmin(false);
					u.setEmail(userInfo.path("profile").path("email").asText());
					u.setFirstName(userInfo.path("profile").path("first_name").asText());
					u.setLastName(userInfo.path("profile").path("last_name").asText());
					u.setFullName(userInfo.path("real_name").asText());
					u.setMainPOI(poiRepo.findByPoiName("Icon at Dulles").get(0));
					u.setWorkPOI(poiRepo.findByPoiName("Revature Office").get(0));
					u.setBanned(false);
					u.setSlackUrl(webhookUrl);
				} else {
					u.setEmail(userInfo.path("profile").path("email").asText());
					u.setFirstName(userInfo.path("profile").path("first_name").asText());
					u.setLastName(userInfo.path("profile").path("last_name").asText());
					u.setFullName(userInfo.path("real_name").asText());
					u.setSlackUrl(webhookUrl);
				}
				userRepo.saveAndFlush(u);
				result = u;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	@Override
	public String createJsonWebToken(User u) {
		String jwt = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			Algorithm alg = Algorithm.HMAC256(jwtSecret);
			String userJson = mapper.writeValueAsString(u);
			jwt = JWT.create()
					.withIssuer("Revature RideShare")
					.withIssuedAt(new Date())
//					.withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
					.withAudience("Revature RideShare AngularJS Client")
					.withClaim("user", userJson)
					.withClaim("admin", u.isAdmin())
					.sign(alg);
		} catch (IllegalArgumentException | UnsupportedEncodingException | JsonProcessingException ex) {
			ex.printStackTrace();
		}
		return jwt;
	}
	
	@Override
	public User verifyJsonWebToken(String token) {
		User u = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			Algorithm alg = Algorithm.HMAC256(jwtSecret);
			JWTVerifier verifier = JWT.require(alg)
					.withIssuer("Revature RideShare")
					.withAudience("Revature RideShare AngularJS Client")
					.build();
			DecodedJWT jwt = verifier.verify(token);
			String userJson = jwt.getClaim("user").asString();
			u = (User) mapper.readValue(userJson, User.class);
		} catch (IllegalArgumentException | IOException ex) {
			ex.printStackTrace();
		} catch (JWTVerificationException ex) {
			ex.printStackTrace();
			System.out.println("Received an invalid token.");
		}
		return u;
	}
	
	@Override
	public User getUserFromToken(String token) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String userJson = JWT.decode(token).getClaim("user").asString();
			return (User) mapper.readValue(userJson, User.class);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			return null;
		}
	}

}
