package com.revature.rideshare.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestController
@RequestMapping("ride")
public class RideController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RideService rideService;

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

	/**
	 * Takes in a Ride ID and deleted the Ride and RideRequest objects
	 * associated.
	 */
	@GetMapping("/request/cancel/{id}")
	public boolean cancelRequest(@PathVariable(value = "id") long id,
			@RequestHeader(name = "Authorization") String token) {
		User u = User.getUserFromToken(token);
		return rideService.cancelRequest(id, u);
	}

	@PostMapping("/request/add")
	public void addRequest(@RequestBody RideRequest req) {
		rideService.addRequest(req);
	}

	@GetMapping("/request/open/{id}")
	public List<RideRequest> getOpenRequests(@PathVariable(value = "id") int id) {
		return rideService.getOpenRequests(id);
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
	public void addOffer(@RequestBody AvailableRide offer) {
		rideService.addOffer(offer);
	}

	@GetMapping("/offer/accept/{id}")
	public boolean acceptOffer(@PathVariable(value = "id") long id,
			@RequestHeader(name = "Authorization") String token) {
		User u = User.getUserFromToken(token);
		return rideService.acceptOffer(id, u);
	}

	/**
	 * Takes in an AvailableRide ID, deletes all associated Rides and reopens
	 * all associated RideRequests.
	 */
	@GetMapping("/offer/cancel/{id}")
	public boolean cancelOffer(@PathVariable(value = "id") long id,
			@RequestHeader(name = "Authorization") String token) {
		User u = User.getUserFromToken(token);
		return rideService.cancelOffer(id, u);
	}

	@GetMapping("/offer/open/{id}")
	public List<AvailableRide> getOpenOffers(@PathVariable(value = "id") int id) {
		return rideService.getOpenOffers(id);
	}

	@GetMapping("/offer/open")
	public List<AvailableRide> getOpenOffers(@RequestHeader(name = "Authorization") String token) {
		User u = User.getUserFromToken(token);
		return rideService.getOpenOffersForUser(u);
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
