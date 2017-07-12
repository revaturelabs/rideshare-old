package com.revature.rideshare.web;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.rideshare.domain.AvailableRide;
import com.revature.rideshare.domain.Ride;
import com.revature.rideshare.domain.RideRequest;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.RideService;
import com.revature.rideshare.service.UserServiceImpl;

@RestController
@RequestMapping("ride")
public class RideController {

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private RideService rideService;

	// ALL RIDES
	@GetMapping
	public List<Ride> getAllRides() {
		return rideService.getAll();
	}
	

	// TODO: move to util class
	private User getUserFromToken(String token) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			String userJson = JWT.decode(token).getClaim("user").asString();
			return (User) mapper.readValue(userJson, User.class);
		} catch (Exception e) {
			return null;
		}
	}
	
	// REQUESTS
	@GetMapping("/request")
	public List<RideRequest> getRequestsForCurrentUser(@RequestHeader(name="Authorization") String token) {
		User u = getUserFromToken(token);
		return rideService.getRequestsForUser(u);
	}

	@GetMapping("/request/accept/{id}")
	public boolean acceptRequest(@PathVariable(value="id") long id, @RequestHeader(name="Authorization") String token) {
		User u = getUserFromToken(token);
		return rideService.acceptRequest(id, u);
	}
    
	@GetMapping("/request/cancel/{id}")
	public boolean cancelRequest(@PathVariable(value="id") long id, @RequestHeader(name="Authorization") String token) {
		User u = getUserFromToken(token);
		return rideService.cancelRequest(id, u);
	}
    
    @PostMapping("/request/add")
    public void addRequest(@RequestBody RideRequest req) {
        rideService.addRequest(req);
    }

	@GetMapping("/request/open/{id}")
	public List<RideRequest> getOpenRequests(@PathVariable(value="id") int id) {
		return rideService.getOpenRequests(id);
	}

	@GetMapping("/request/active")
	public List<Ride> getActiveRequestsForCurrentUser(@RequestHeader(name="Authorization") String token) {
		User u = getUserFromToken(token);
		return rideService.getActiveRequestsForUser(u);
	}

	@GetMapping("/request/history")
	public List<Ride> getRequestHistoryForCurrentUser(@RequestHeader(name="Authorization") String token) {
		User u = getUserFromToken(token);
		return rideService.getRequestHistoryForUser(u);
	}


	// OFFERS
	@GetMapping("/offer")
	public List<AvailableRide> getOffersForCurrentUser(@RequestHeader(name="Authorization") String token) {
		User u = getUserFromToken(token);
		return rideService.getOffersForUser(u);
	}

    @PostMapping("/offer/add")
    public void addOffer(@RequestBody AvailableRide offer) {
        rideService.addOffer(offer);
    }

	@GetMapping("/offer/accept/{id}")
	public boolean acceptOffer(@PathVariable(value="id") long id, @RequestHeader(name="Authorization") String token) {
		User u = getUserFromToken(token);
		return rideService.acceptOffer(id, u);
	}
    
	@GetMapping("/offer/cancel/{id}")
	public boolean cancelOffer(@PathVariable(value="id") long id, @RequestHeader(name="Authorization") String token) {
		User u = getUserFromToken(token);
		return rideService.cancelOffer(id, u);
	}
    
	@GetMapping("/offer/open/{id}")
	public List<AvailableRide> getOpenOffers(@PathVariable(value="id") int id) {
		return rideService.getOpenOffers(id);
	}

	@GetMapping("/offer/active")
	public List<Ride> getActiveOffersForCurrentUser(@RequestHeader(name="Authorization") String token) {
		User u = getUserFromToken(token);
		return rideService.getActiveOffersForUser(u);
	}

	@GetMapping("/offer/history")
	public List<Ride> getOfferHistoryForCurrentUser(@RequestHeader(name="Authorization") String token) {
		User u = getUserFromToken(token);
		return rideService.getOfferHistoryForUser(u);
	}
}
