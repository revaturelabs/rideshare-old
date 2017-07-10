package com.revature.rideshare.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.revature.rideshare.dao.AvailableRideRepository;
import com.revature.rideshare.dao.RideRepository;
import com.revature.rideshare.dao.RideRequestRepository;
import com.revature.rideshare.domain.AvailableRide;
import com.revature.rideshare.domain.Ride;
import com.revature.rideshare.domain.RideRequest;
import com.revature.rideshare.domain.User;

@Component("rideService")
public class RideService {

	@Autowired
	private RideRepository rideRepo;
	
	@Autowired
	private RideRequestRepository rideReqRepo;
	
	@Autowired
	private AvailableRideRepository availRideRepo;
	
	public List<Ride> getAll() {
			return rideRepo.findAll();
	}
	
	
	// REQUESTS
	public void addRequest(RideRequest req) {
		rideReqRepo.saveAndFlush(req);
	}

	public List<RideRequest> getOpenRequests() {
//		List<Ride> openRides = rideRepo.findAllByRequestNotNullAndAvailRideNull();
//		List<RideRequest> openReqs = new ArrayList<RideRequest>();
//		
//		for (Ride r : openRides) {
//			openReqs.add(r.getRequest());
//		}
//
//		return openReqs;
//		return rideReqRepo.findOpen();
		return null;
	}

	public List<Ride> getHistoryForUser(User u) {
		return rideRepo.findByAvailRideCarUserOrRequestUser(u, u);
	}
	
	public List<RideRequest> getRequestsForUser(User u) {
		return rideReqRepo.findByUser(u);
	}

	public List<Ride> getActiveRequestsForUser(User u) {
		List<Ride> allRides = rideRepo.findByRequestUser(u);
		List<Ride> activeRides = new ArrayList<Ride>();
		
		for (Ride r : allRides) {
			if (r.getWasSuccessful() == null) {
				activeRides.add(r);
			}
		}
		
		return activeRides;
	}

	public List<Ride> getRequestHistoryForUser(User u) {
		List<Ride> allRides = rideRepo.findByRequestUser(u);
		List<Ride> completedRides = new ArrayList<Ride>();
		
		for (Ride r : allRides) {
			if (r.getWasSuccessful() != null) {
				completedRides.add(r);
			}
		}
		
		return completedRides;
	}

	
	
	// OFFERS
	public List<AvailableRide> getOffersForUser(User u) {
		return availRideRepo.findByCarUser(u);
	}

	public void addOffer(AvailableRide offer) {
		availRideRepo.saveAndFlush(offer);
	}

	public List<AvailableRide> getOpenOffers() {
//		List<Ride> openRides = rideRepo.findAllByAvailRideNotNullAndRequestNull();
//		List<AvailableRide> openOffers = new ArrayList<AvailableRide>();
//		
//		for (Ride r : openRides) {
//			openOffers.add(r.getAvailRide());
//		}
//
//		return openOffers;
//		return availRideRepo.findOpen();
		return null;
	}
	
	public List<Ride> getActiveOffersForUser(User u) {
		List<Ride> allRides = rideRepo.findByAvailRideCarUser(u);
		List<Ride> activeRides = new ArrayList<Ride>();
		
		for (Ride r : allRides) {
			if (r.getWasSuccessful() == null) {
				activeRides.add(r);
			}
		}
		
		return activeRides;
	}

	public List<Ride> getOfferHistoryForUser(User u) {
		List<Ride> allRides = rideRepo.findByAvailRideCarUser(u);
		List<Ride> completedRides = new ArrayList<Ride>();
		
		for (Ride r : allRides) {
			if (r.getWasSuccessful() != null) {
				completedRides.add(r);
			}
		}
		
		return completedRides;
	}

	
}
