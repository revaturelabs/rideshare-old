package com.revature.rideshare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.revature.rideshare.dao.RideRepository;
import com.revature.rideshare.domain.Ride;

@Component("rideService")
public class RideService {

	private final RideRepository rideRepo;
	
	@Autowired
	public RideService(RideRepository rideRepo){
		this.rideRepo = rideRepo;
	}
	
	public List<Ride> getAll() {
			return rideRepo.findAll();
	}

}