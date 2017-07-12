package com.revature.rideshare.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.domain.AvailableRide;
import com.revature.rideshare.domain.Ride;
import com.revature.rideshare.domain.RideRequest;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.RideService;
import com.revature.rideshare.service.UserService;

@RestController
@RequestMapping("ride")
public class RideController {

	@Autowired
	private RideService rideService;
	
	@Autowired
	private UserService userService;

	// ALL RIDES
	@GetMapping
	public List<Ride> getAllRides() {
		return rideService.getAll();
	}

	// REQUESTS
	@GetMapping("/request")
	public List<RideRequest> getRequestsForCurrentUser(@RequestHeader(name = "Authorization") String token) {
		User u = User.getUserFromToken(token);
		return rideService.getRequestsForUser(u);
	}

	@GetMapping("/request/accept/{id}")
	public boolean acceptRequest(@PathVariable(value = "id") long id,
			@RequestHeader(name = "Authorization") String token) {
		User u = User.getUserFromToken(token);
		return rideService.acceptRequest(id, u);
	}

	@GetMapping("/request/cancel/{id}")
	public boolean cancelRequest(@PathVariable(value = "id") long id,
			@RequestHeader(name = "Authorization") String token) {
		User u = User.getUserFromToken(token);
		return rideService.cancelRequest(id, u);
	}

	@PostMapping("/request/add")
	public boolean addRequest(@RequestBody RideRequest req) {
		return rideService.addRequest(req);
	}

	@GetMapping("/request/open")
	public List<RideRequest> getOpenRequests() {
		return rideService.getOpenRequests();
	}

	@GetMapping("/request/active")
	public List<Ride> getActiveRequestsForCurrentUser(@RequestHeader(name = "Authorization") String token) {
		User u = User.getUserFromToken(token);
		return rideService.getActiveRequestsForUser(u);
	}

	@GetMapping("/request/history")
	public List<Ride> getRequestHistoryForCurrentUser(@RequestHeader(name = "Authorization") String token) {
		User u = User.getUserFromToken(token);
		return rideService.getRequestHistoryForUser(u);
	}

	// OFFERS
	@GetMapping("/offer")
	public List<AvailableRide> getOffersForCurrentUser(@RequestHeader(name = "Authorization") String token) {
		User u = User.getUserFromToken(token);
		return rideService.getOffersForUser(u);
	}

	@PostMapping("/offer/add")
	public boolean addOffer(@RequestBody AvailableRide offer) {
		return rideService.addOffer(offer);
	}

	@GetMapping("/offer/accept/{id}")
	public boolean acceptOffer(@PathVariable(value = "id") long id,
			@RequestHeader(name = "Authorization") String token) {
		User u = User.getUserFromToken(token);
		return rideService.acceptOffer(id, u);
	}

	@GetMapping("/offer/cancel/{id}")
	public boolean cancelOffer(@PathVariable(value = "id") long id,
		@RequestHeader(name = "Authorization") String token) {
		User u = User.getUserFromToken(token);
		return rideService.cancelOffer(id, u);
	}

	@GetMapping("/offer/open")
	public List<AvailableRide> getOpenOffers() {
		return rideService.getOpenOffers();
	}

	@GetMapping("/offer/active")
	public List<Ride> getActiveOffersForCurrentUser(@RequestHeader(name = "Authorization") String token) {
		User u = User.getUserFromToken(token);
		return rideService.getActiveOffersForUser(u);
	}

	@GetMapping("/offer/history")
	public List<Ride> getOfferHistoryForCurrentUser(@RequestHeader(name = "Authorization") String token) {
		User u = User.getUserFromToken(token);
		return rideService.getOfferHistoryForUser(u);
	}
}