package com.revature.rideshare.web;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
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
	public List<Ride> getHistoryForCurrentUser(Principal principal) {
		OAuth2Authentication oa = (OAuth2Authentication) principal;
		System.out.println("name " + oa.getName());
		System.out.println("cred " + oa.getCredentials());
		System.out.println("deet " + oa.getDetails());
		System.out.println("prin " + oa.getPrincipal());
//		User u = (User) principal;
//		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
//		return rideService.getHistoryForUser(u);
		return null;
	}

	// REQUESTS
	@GetMapping("/request")
	public List<RideRequest> getRequestsForCurrentUser(Principal principal) {
//		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
		User u = (User) principal;
		return rideService.getRequestsForUser(u);
	}

	@GetMapping("/request/open")
	public List<RideRequest> getOpenRequests() {
		return rideService.getOpenRequests();
	}

	@GetMapping("/request/active")
	public List<Ride> getActiveRequestsForCurrentUser(Principal principal) {
//		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
		User u = (User) principal;
		return rideService.getActiveRequestsForUser(u);
	}

	@GetMapping("/request/history")
	public List<Ride> getRequestHistoryForCurrentUser(Principal principal) {
//		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
		User u = (User) principal;
		return rideService.getRequestHistoryForUser(u);
	}


	// OFFERS
	@GetMapping("/offer")
	public List<AvailableRide> getOffersForCurrentUser(Principal principal) {
//		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
		User u = (User) principal;
		return rideService.getOffersForUser(u);
	}

	@GetMapping("/offer/open")
	public List<AvailableRide> getOpenOffers() {
		return rideService.getOpenOffers();
	}

	@GetMapping("/offer/active")
	public List<Ride> getActiveOffersForCurrentUser(Principal principal) {
//		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
		User u = (User) principal;
		return rideService.getActiveOffersForUser(u);
	}

	@GetMapping("/offer/history")
	public List<Ride> getOfferHistoryForCurrentUser(Principal principal) {
//		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
		User u = (User) principal;
		return rideService.getOfferHistoryForUser(u);
	}

}
