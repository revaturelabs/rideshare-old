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
import com.revature.rideshare.domain.User;
import com.revature.rideshare.exception.BannedUserException;
import com.revature.rideshare.exception.SlackApiException;

@Component
public class AuthServiceImpl implements AuthService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
//	TODO: start using environment variables when this application is deployed
//	@Value("#{systemEnvironment['RIDESHARE_JWT_SECRET']}")
	private String jwtSecret = "Richie is obsessed with chickens!";
	@Value("${slack.identity.client.clientId}")
	private String slackAppId;
	@Value("${slack.identity.client.clientSecret}")
	private String slackAppSecret;
	@Value("${slack.verificationToken}")
	private String slackAppVerificationToken;
	@Value("${slack.teamId}")
	private String slackAppTeamId;
	@Value("${deploy.url}")
	private String rideshareUrl;
	private String loginRedirectUrl = "https://localhost:8443/auth/login";
	private String integrationRedirectUrl = "https://localhost:8443/auth/integrate";
	
//	@Autowired
//	OAuth2ClientContext oauth2ClientContext;
	
//	@Autowired
//	AuthorizationCodeResourceDetails slackResource;
	
	@Autowired
	UserService userService;
	
	@Autowired
	PointOfInterestRepository poiRepo;
	
	public AuthServiceImpl() {
		super();
	}

	@Override
	public void setUserService(UserService userService) {
		this.userService = userService;
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
	
	/*
	 * use this when dealing with requesting the identity scopes to authenticate a user
	 */
	@Override
	public String getSlackAccessToken(String code) throws SlackApiException {
		RestTemplate client = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();
		String result = null;
		String url = "https://slack.com/api/oauth.access";
		String requestUrl = url + "?client_id=" + slackAppId + "&client_secret=" + slackAppSecret + "&code=" + code
				+ "&redirect_uri=" + loginRedirectUrl;
//		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
//		requestBody.add("client_id", slackAppId);
//		requestBody.add("client_secret", slackAppSecret);
//		requestBody.add("code", code);
//		ResponseEntity<String> response = client.postForEntity(url, requestBody, String.class);
		ResponseEntity<String> response = client.getForEntity(requestUrl, String.class);
		System.out.println(response.getBody());
		try {
			JsonNode body = mapper.readTree(response.getBody());
			if (body.path("ok").asBoolean()) {
				result = body.path("access_token").asText();
			} else {
				throw new SlackApiException("Failed to retrieve Slack access token - " + body.path("error").asText());
			}
		} catch (IOException ex) {
			logger.error("", ex);
		}
		return result;
	}
	
	/*
	 * use this when requesting the incoming-webhook and commands scopes to integrate with slack
	 */
	@Override
	public JsonNode getSlackAccessResponse(String code) throws SlackApiException {
		RestTemplate client = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode result = null;
		String url = "https://slack.com/api/oauth.access";
		String requestUrl = url + "?cient_id=" + slackAppId + "&client_secret=" + slackAppSecret + "&code=" + code
				+ "&redirect_uri=" + integrationRedirectUrl;
//		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
//		requestBody.add("client_id", slackAppId);
//		requestBody.add("client_secret", slackAppSecret);
//		requestBody.add("code", code);
//		ResponseEntity<String> response = client.postForEntity(url, requestBody, String.class);
		ResponseEntity<String> response = client.getForEntity(requestUrl, String.class);
		try {
			JsonNode body = mapper.readTree(response.getBody());
			if (body.path("ok").asBoolean()) {
				result = body;
			} else {
				throw new SlackApiException("Failed to get access response from Slack - " + body.path("error").asText());
			}
		} catch (IOException ex) {
			logger.error("", ex);
		}
		return result;
	}
	
	@Override
	public String getUserIdentity(String token) throws SlackApiException {
		RestTemplate client = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();
		String result = null;
		String url = "https://slack.com/api/users.identity";
		String requestUrl = url + "?token=" + token;
//		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
//		requestBody.add("token", token);
//		ResponseEntity<String> response = client.postForEntity(url, requestBody, String.class);
		ResponseEntity<String> response = client.getForEntity(requestUrl, String.class);
		try {
			JsonNode body = mapper.readTree(response.getBody());
			if (body.path("ok").asBoolean()) {
				result = body.path("user").path("id").asText();
			} else {
				throw new SlackApiException("Failed to get user identity from Slack - " + body.path("error").asText());
			}
		} catch (IOException ex) {
			logger.error("", ex);
		}
		return result;
	}
	
	@Override
	public JsonNode getUserProfile(String token, String slackId) throws SlackApiException {
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
	public JsonNode getUserInfo(String token, String slackId) throws SlackApiException {
		RestTemplate client = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode result = null;
		String url = "https://slack.com/api/users.info";
//		String requestUrl = url + "?token=" + token + "&user=" + slackId;
//		ResponseEntity<String> response = client.getForEntity(requestUrl, String.class);
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
	public User getUserAccount(String slackId, JsonNode userInfo) throws BannedUserException {
		User u = userService.getUserBySlackId(slackId);
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
			userService.addUser(u);
		} else {
			u.setEmail(userInfo.path("profile").path("email").asText());
			u.setFirstName(userInfo.path("profile").path("first_name").asText());
			u.setLastName(userInfo.path("profile").path("last_name").asText());
			u.setFullName(userInfo.path("real_name").asText());
			if (u.isBanned()) {
				throw new BannedUserException("This user has been banned from the application.");
			}
			userService.updateUser(u);
		}
		return u;
	}
	
	@Override
	public User integrateUser(User u, JsonNode accessResponse) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String slackId = accessResponse.path("user_id").asText();
			JsonNode incomingWebhook = mapper.readTree(accessResponse.path("incoming_webhook").asText());
			String webhookUrl = incomingWebhook.path("url").asText();
			if (u.getSlackId().equals(slackId)) {
				u.setSlackUrl(webhookUrl);
				userService.updateUser(u);
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
			logger.error("", ex);
		} catch (JWTVerificationException ex) {
			logger.error("Got an invalid JSON web token", ex);
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
			logger.error("Failed to get user from JSON web token", ex);
			return null;
		}
	}

}
