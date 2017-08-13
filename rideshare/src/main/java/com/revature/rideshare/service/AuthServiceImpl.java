package com.revature.rideshare.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.rideshare.dao.PointOfInterestRepository;
import com.revature.rideshare.dao.UserRepository;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.exception.SlackApiException;

@Component
public class AuthServiceImpl implements AuthService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("#{systemEnvironment['RIDESHARE_JWT_SECRET']}")
	private String jwtSecret;
	@Value("#{systemEnvironment['RIDESHARE_SLACK_ID']}")
	private String slackAppId;
	@Value("#{systemEnvironment['RIDESHARE_SLACK_SECRET']}")
	private String slackAppSecret;
	@Value("#{systemEnvironment['RIDESHARE_SLACK_VERIFICATION']}")
	private String slackAppVerificationToken;
	@Value("#{systemEnvironment['RIDESHARE_SLACK_TEAM']}")
	private String slackAppTeamId;
	
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

	@Override
	public JsonNode getSlackIdentity(String token) throws SlackApiException {
		RestTemplate client = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode result = null;
		String url = "https://slack.com/api/users.identity";
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
		requestBody.add("token", token);
		ResponseEntity<String> response = client.postForEntity(url, requestBody, String.class);
		try {
			JsonNode body = mapper.readTree(response.getBody());
			if (body.path("ok").asBoolean()) {
				result = body.path("user");
			} else {
				throw new SlackApiException("Failed to get user identity from Slack - " + body.path("error").asText());
			}
		} catch (IOException ex) {
			logger.error("", ex);
		}
		return result;
	}
	
	@Override
	public JsonNode getSlackProfile(String token, String slackId) throws SlackApiException {
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
			} else {
				throw new SlackApiException("Failed to get user profile from Slack - " + body.path("error").asText());
			}
		} catch (IOException ex) {
			logger.error("", ex);
		}
		return result;
	}
	
	@Override
	public JsonNode getSlackInfo(String token, String slackId) throws SlackApiException {
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
			} else {
				throw new SlackApiException("Failed to get user info from Slack - " + body.path("error").asText());
			}
		} catch (IOException ex) {
			logger.error("", ex);
		}
		return result;
	}
	
	@Override
	public User getUserAccount(JsonNode userIdentity, JsonNode userInfo) {
		String slackId = userIdentity.path("id").asText();
		String firstname = null;
		String lastname = null;
		if (userInfo != null) {
			firstname = userInfo.path("profile").path("first_name").asText();
			lastname = userInfo.path("profile").path("last_name").asText();
		}
		User u = userRepo.findBySlackId(slackId);
		if (u == null) {
			u = new User();
			u.setSlackId(slackId);
			u.setAdmin(false);
			u.setEmail(userIdentity.path("email").asText());
			u.setFirstName(firstname);
			u.setLastName(lastname);
			u.setFullName(userIdentity.path("name").asText());
			u.setMainPOI(poiRepo.findByPoiName("Icon at Dulles").get(0));
			u.setWorkPOI(poiRepo.findByPoiName("Revature Office").get(0));
			u.setBanned(false);
			userRepo.saveAndFlush(u);
			logger.info("Created new account for " + u);
		} else {
			u.setEmail(userIdentity.path("email").asText());
			u.setFirstName(firstname);
			u.setLastName(lastname);
			u.setFullName(userIdentity.path("name").asText());
			userRepo.saveAndFlush(u);
		}
		return u;
	}
	
	
	
	@Override
	public User getUserAccount(String fullname, String slackId, String email) {
		User u = userRepo.findBySlackId(slackId);
		if (u == null) {
			u = new User();
			u.setSlackId(slackId);
			u.setAdmin(false);
			u.setEmail(email);
			u.setFullName(fullname);
			u.setMainPOI(poiRepo.findByPoiName("Icon at Dulles").get(0));
			u.setWorkPOI(poiRepo.findByPoiName("Revature Office").get(0));
			u.setBanned(false);
			userRepo.saveAndFlush(u);
			logger.info("Created new account for " + u);
		} else {
			u.setEmail(email);
			u.setFullName(fullname);
			userRepo.saveAndFlush(u);
		}
		return u;
	}

	/*
	 * This method is currently unused.
	 */
	@Override
	public User integrateUser(User u, JsonNode accessResponse) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String slackId = accessResponse.path("user_id").asText();
			JsonNode incomingWebhook = mapper.readTree(accessResponse.path("incoming_webhook").asText());
			String webhookUrl = incomingWebhook.path("url").asText();
			if (u.getSlackId().equals(slackId)) {
				u.setSlackUrl(webhookUrl);
				userRepo.saveAndFlush(u);
			}
		} catch (IOException ex) {
			logger.error("Failed to activate slack integration for " + u, ex);
		}
		return u;
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
					.withAudience("Revature RideShare AngularJS Client")
					.withClaim("user", userJson)
					.sign(alg);
		} catch (IllegalArgumentException | UnsupportedEncodingException | JsonProcessingException ex) {
			logger.error("", ex);
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
			logger.error("Failed to get user from JSON web token", ex);
		} catch (JWTVerificationException ex) {
			logger.error("Got an invalid JSON web token", ex);
		}
		return u;
	}
	
	@Override
	public User getUserFromToken(String token) {
		return verifyJsonWebToken(token);
	}

}
