package com.revature.rideshare.web;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	
	
	@GetMapping("/history")
	public List<Ride> getHistoryForCurrentUser(Principal principal) {
	//		User u = (User) principal;
	//		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
		User u = userService.getUser(1);

		return rideService.getHistoryForUser(u);
	}

	// REQUESTS
    @PostMapping("/request/add")
    public void addRequest(@RequestBody RideRequest req){
        rideService.addRequest(req);
    }

	@GetMapping("/request")
	public List<RideRequest> getRequestsForCurrentUser(Principal principal) {
//		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
//		User u = (User) principal;
		User u = userService.getUser(1);
		return rideService.getRequestsForUser(u);
	}

	@GetMapping("/request/open")
	public List<RideRequest> getOpenRequests() {
		return rideService.getOpenRequests();
	}

	@GetMapping("/request/active")
	public List<Ride> getActiveRequestsForCurrentUser(Principal principal) {
//		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
//		User u = (User) principal;
		User u = userService.getUser(1);
		return rideService.getActiveRequestsForUser(u);
	}

	@GetMapping("/request/history")
	public List<Ride> getRequestHistoryForCurrentUser(Principal principal) {
//		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
//		User u = (User) principal;
		User u = userService.getUser(1);
		return rideService.getRequestHistoryForUser(u);
	}


	// OFFERS
	@GetMapping("/offer")
	public List<AvailableRide> getOffersForCurrentUser(Principal principal) {
//		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
//		User u = (User) principal;
		User u = userService.getUser(1);
		return rideService.getOffersForUser(u);
	}

//	@RequestMapping("/offer/accept/{id}")
//	public @ResponseBody User getUser(@PathVariable(value = "id") long id) {
//		return userService.getUser(id);
//	}
    
    @PostMapping("/offer/add")
    public void addOffer(@RequestBody AvailableRide offer){
        rideService.addOffer(offer);
    }

	@GetMapping("/offer/open")
	public List<AvailableRide> getOpenOffers() {
		return rideService.getOpenOffers();
	}

	@GetMapping("/offer/active")
	public List<Ride> getActiveOffersForCurrentUser(Principal principal) {
//		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
//		User u = (User) principal;
		User u = userService.getUser(1);
		return rideService.getActiveOffersForUser(u);
	}

	@GetMapping("/offer/history")
	public List<Ride> getOfferHistoryForCurrentUser(Principal principal) {
//		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
//		User u = (User) principal;
		User u = userService.getUser(1);
		return rideService.getOfferHistoryForUser(u);
	}

}
