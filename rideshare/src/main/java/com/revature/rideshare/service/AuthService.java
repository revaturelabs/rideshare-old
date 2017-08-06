package com.revature.rideshare.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.revature.rideshare.dao.PointOfInterestRepository;
import com.revature.rideshare.dao.UserRepository;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.exception.SlackApiException;

public interface AuthService {

	void setUserRepo(UserRepository userRepo);

	void setPoiRepo(PointOfInterestRepository poiRepo);

	JsonNode getSlackIdentity(String token) throws SlackApiException;

	JsonNode getSlackProfile(String token, String slackId) throws SlackApiException;

	JsonNode getSlackInfo(String token, String slackId) throws SlackApiException;

	User getUserAccount(JsonNode userIdentity, JsonNode userInfo);
	
	User getUserAccount(String fullname, String slackId, String email);

	User integrateUser(User u, JsonNode accessResponse);

	String createJsonWebToken(User u);

	User verifyJsonWebToken(String token);

	User getUserFromToken(String token);

}