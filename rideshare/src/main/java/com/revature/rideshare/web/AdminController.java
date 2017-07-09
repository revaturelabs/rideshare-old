package com.revature.rideshare.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.domain.Ride;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.RideService;
import com.revature.rideshare.service.UserService;

@RestController
@RequestMapping("admin")
public class AdminController {

	@Autowired
	private RideService rideService;
	
	 @Autowired
	 private UserService userService;
	
	 //
	@GetMapping
	public List<Ride> getAllRides() {
		return rideService.getAll();
	}
	
	@GetMapping
    public List<User> getAll(){
        return userService.getAll();
    }
	
	
}
