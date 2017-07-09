package com.revature.rideshare.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.revature.rideshare.dao.RideRepository;
import com.revature.rideshare.domain.Ride;
import com.revature.rideshare.domain.User;

@Component("rideService")
public class RideService {

	@Autowired
	private RideRepository rideRepo;
	
	public List<Ride> getAll() {
			return rideRepo.findAll();
	}
	
	public List<Ride> getActiveRidesForUser(User u) {
		List<Ride> rides = new ArrayList<Ride>();
		
		rideRepo.findByRequestUserUserId(u.getUserId());
		
		return rides;
	}

}