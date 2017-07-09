package com.revature.rideshare.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.domain.AvailableRide;
import com.revature.rideshare.domain.Ride;
import com.revature.rideshare.domain.RideRequest;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.RideService;

@RestController
@RequestMapping("ride")
public class RideController {

	@Autowired
	private RideService rideService;

	@GetMapping
	public List<Ride> getAllRides() {
		return rideService.getAll();
	}

	
	@GetMapping("/history")
	public List<Ride> getHistoryForCurrentUser(Authentication authentication) {
		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
		return rideService.getHistoryForUser(u);
	}

	// REQUESTS
	@GetMapping("/request")
	public List<RideRequest> getRequestsForCurrentUser(Authentication authentication) {
		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
		return rideService.getRequestsForUser(u);
	}

	@GetMapping("/request/open")
	public List<RideRequest> getOpenRequests() {
		return rideService.getOpenRequests();
	}

	@GetMapping("/request/active")
	public List<Ride> getActiveRequestsForCurrentUser(Authentication authentication) {
		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
		return rideService.getActiveRequestsForUser(u);
	}

	@GetMapping("/request/history")
	public List<Ride> getRequestHistoryForCurrentUser(Authentication authentication) {
		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
		return rideService.getRequestHistoryForUser(u);
	}


	// OFFERS
	@GetMapping("/offer")
	public List<AvailableRide> getOffersForCurrentUser(Authentication authentication) {
		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
		return rideService.getOffersForUser(u);
	}

	@GetMapping("/offer/active")
	public List<Ride> getActiveOffersForCurrentUser(Authentication authentication) {
		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
		return rideService.getActiveOffersForUser(u);
	}

	@GetMapping("/offer/history")
	public List<Ride> getOfferHistoryForCurrentUser(Authentication authentication) {
		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
		return rideService.getOfferHistoryForUser(u);
	}

}
