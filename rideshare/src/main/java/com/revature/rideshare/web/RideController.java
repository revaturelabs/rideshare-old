package com.revature.rideshare.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.domain.Ride;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.RideService;

@RestController
@RequestMapping("ride")
public class RideController {


	@Autowired
	private RideService rideService;

	@GetMapping
	public List<Ride> getAllRides() {
		List<Ride> rides = 	rideService.getAll();
		
		return rides;
	}
	
	@GetMapping("/ridesForCurrentUser")
	public List<Ride> getRidesForCurrentUser(Authentication authentication) {
		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
		return rideService.getActiveRidesForUser(u);
	}
	
}
