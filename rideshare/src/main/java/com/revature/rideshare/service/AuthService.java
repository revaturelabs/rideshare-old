package com.revature.rideshare.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.revature.rideshare.dao.PointOfInterestRepository;
import com.revature.rideshare.domain.User;

public interface AuthService {

	void setUserService(UserService userService);

	void setPoiRepo(PointOfInterestRepository poiRepo);

	/*
	 * use this when dealing with requesting the identity scopes to authenticate a user
	 */
	String getSlackAccessToken(String code);

	/*
	 * use this when requesting the incoming-webhook and commands scopes to integrate with slack
	 */
	JsonNode getSlackAccessResponse(String code);

	String getUserIdentity(String token);

	JsonNode getUserProfile(String token, String slackId);

	JsonNode getUserInfo(String token, String slackId);

	User authenticateUser(String code);

	User getIntegratedUser(String code);

	String createJsonWebToken(User u);

	User verifyJsonWebToken(String token);

	User getUserFromToken(String token);

}