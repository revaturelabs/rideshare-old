package com.revature.rideshare.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.revature.rideshare.dao.PointOfInterestRepository;
import com.revature.rideshare.dao.UserRepository;
import com.revature.rideshare.domain.User;

public interface AuthService {

	void setUserRepo(UserRepository userRepo);

	void setPoiRepo(PointOfInterestRepository poiRepo);

	String getSlackAccessToken(String code);

	JsonNode getSlackAccessResponse(String code);

	String getUserIdentity(String token);

	JsonNode getUserProfile(String token, String slackId);

	JsonNode getUserInfo(String token, String slackId);

	User getAuthenticatedUser(String code);

	String createJsonWebToken(User u);

	User verifyJsonWebToken(String token);

	User getUserFromToken(String token);

}