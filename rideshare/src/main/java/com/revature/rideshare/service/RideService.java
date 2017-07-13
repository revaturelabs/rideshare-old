package com.revature.rideshare.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.revature.rideshare.dao.AvailableRideRepository;
import com.revature.rideshare.dao.CarRepository;
import com.revature.rideshare.dao.RideRepository;
import com.revature.rideshare.dao.RideRequestRepository;
import com.revature.rideshare.domain.AvailableRide;
import com.revature.rideshare.domain.Car;
import com.revature.rideshare.domain.Ride;
import com.revature.rideshare.domain.RideRequest;
import com.revature.rideshare.domain.RideRequest.RequestStatus;
import com.revature.rideshare.domain.User;

@Component("rideService")
public class RideService {

	@Autowired
	private RideRepository rideRepo;
	
	@Autowired
	private RideRequestRepository rideReqRepo;
	
	@Autowired
	private AvailableRideRepository availRideRepo;

	@Autowired
	private CarRepository carRepo;

	public List<Ride> getAll() {
			return rideRepo.findAll();
	}
	
	// REQUESTS
	public void addRequest(RideRequest req) {
		rideReqRepo.saveAndFlush(req);
	}
	
	public boolean acceptRequest(long id, User u) {
		// get request from id and satisfy it
		RideRequest req = rideReqRepo.getOne(id);
		req.setStatus(RideRequest.RequestStatus.SATISFIED);
		rideReqRepo.saveAndFlush(req);
		
		// duplicate request as availRide
		AvailableRide offer = new AvailableRide();
		Car car = carRepo.findByUser(u);
		offer.setCar(car);
		offer.setSeatsAvailable((short)1);
		offer.setPickupPOI(req.getPickupLocation());
		offer.setDropoffPOI(req.getDropOffLocation());
		offer.setOpen(false);
		offer.setTime(req.getTime());
		availRideRepo.saveAndFlush(offer);

		// create ride obj from req and avail
		Ride ride = new Ride();
		ride.setAvailRide(offer);
		ride.setRequest(req);
		
		try {
			rideRepo.saveAndFlush(ride);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean cancelRequest(long id, User u) {
		return false;
	}

	public List<RideRequest> getOpenRequests() {
		return null;
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

	public List<AvailableRide> getOffersForUser(User u) {
		return availRideRepo.findByCarUser(u);
	}

	public void addOffer(AvailableRide offer) {
		availRideRepo.saveAndFlush(offer);
	}

	public boolean acceptOffer(long id, User u) {
		// get request from id and satisfy it
		AvailableRide offer = availRideRepo.getOne(id);

		// if car is full set open to false
		Long inRide = rideRepo.countByAvailRide(offer) + 1;
		if (offer.getSeatsAvailable() <= inRide) {
			offer.setOpen(false);
		}

		availRideRepo.saveAndFlush(offer);
		
		// duplicate offer as request
		RideRequest req = new RideRequest();
		req.setUser(u);
		req.setPickupLocation(offer.getPickupPOI());
		req.setDropOffLocation(offer.getDropoffPOI());
		req.setTime(offer.getTime());
		req.setStatus(RequestStatus.SATISFIED);
		rideReqRepo.saveAndFlush(req);


		// create ride obj from req and avail
		Ride ride = new Ride();
		ride.setAvailRide(offer);
		ride.setRequest(req);
		
		try {
			rideRepo.saveAndFlush(ride);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean cancelOffer(long id, User u) {
		return false;
	}

	public List<AvailableRide> getOpenOffers() {
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
