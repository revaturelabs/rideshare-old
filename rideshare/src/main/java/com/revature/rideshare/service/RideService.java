package com.revature.rideshare.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.revature.rideshare.dao.AvailableRideRepository;
import com.revature.rideshare.dao.CarRepository;
import com.revature.rideshare.dao.RideRepository;
import com.revature.rideshare.dao.RideRequestRepository;
import com.revature.rideshare.domain.AvailableRide;
import com.revature.rideshare.domain.Car;
import com.revature.rideshare.domain.PointOfInterest;
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

	@Autowired
	private PointOfInterestService poiService;

	/**
	 * Persists a RideRequest to the database.
	 *
	 * @param RideRequest
	 *            req a RideRequest object to be persisted.
	 *
	 * @return boolean returns true on success and false on failure.
	 */
	public boolean addRequest(RideRequest req) {
		RideRequest temp = rideReqRepo.saveAndFlush(req);
		if (temp == null) {
			return false;
		}
		return true;
	}

	/**
	 * Returns a list of all (active and inactive) rides.
	 *
	 * @return List<Ride> A list of Rides.
	 */
	public List<Ride> getAll() {
		return rideRepo.findAll();
	}

	/**
	 * Returns a list of all active(not complete) rides.
	 *
	 * @return List<Ride> A list of active Rides.
	 */
	public List<Ride> getAllActiveRides() {
		return rideRepo.findByWasSuccessfulNull();
	}

	/**
	 * Returns a list of all inactive(completed) rides.
	 *
	 * @return List<Ride> A list of inactive Rides.
	 */
	public List<Ride> getAllInactiveRides() {
		return rideRepo.findByWasSuccessfulNotNull();
	}

	/**
	 * Takes in the RideRequest id and creates a Ride to associate said
	 * RideRequest with. If the user does not have an open AvailableRide to link
	 * with the Ride, one will be created with a default of 1 available seat.
	 *
	 * @param long
	 *            id the id of the AvailableRide to assign the Request to.
	 * 
	 * @param User
	 *            u the active user.
	 * 
	 * @return boolean returns true on success, false on failure.
	 */
	public boolean acceptRequest(long id, User u) {
		// get request from id and satisfy it
		RideRequest req = rideReqRepo.getOne(id);
		req.setStatus(RideRequest.RequestStatus.SATISFIED);
		rideReqRepo.saveAndFlush(req);

		// TODO: Optimize the creation of AvailableRides. There is currently no
		// checks
		// for if this driver already has an offer opened for these POIs.
		AvailableRide offer = new AvailableRide();
		Car car = carRepo.findByUser(u);
		offer.setCar(car);
		offer.setSeatsAvailable((short) 1);
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

	/**
	 * Takes in a Ride ID. Closes the open RideRequest and deletes the Ride.
	 *
	 * @param long
	 *            id The id of the request to cancel.
	 * @return true on success, false on failure.
	 */
	public boolean cancelRequest(long id, User u) {
		try {
			Ride ride = rideRepo.findOne(id);
			RideRequest req = ride.getRequest();

			AvailableRide availRide = ride.getAvailRide();
			if (!availRide.isOpen()) {
				// reopen if closed (because a seat is now available)
				availRide.setOpen(true);
			}

			rideRepo.delete(ride);
			rideReqRepo.delete(req);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Takes in the main poi's id and returns all open requests starting at said
	 * id. List is ordered by closest to farthest POI and within each of those,
	 * by date.
	 *
	 * @param int
	 *            id The id of the main POI(Point of Interest).
	 * @return List<RideRequest> A list of Ride Requests.
	 */
	public List<RideRequest> getOpenRequests(int poiId) {
		List<RideRequest> openReqs = rideReqRepo.findByStatus(RequestStatus.OPEN);

		Collections.sort(openReqs); // sorting by date.

		// Sorting by closest to farthest POI
		PointOfInterest temp = poiService.getPoi(poiId);
		openReqs = sortRequestsByPOI(openReqs, temp);

		return openReqs;
	}

	/**
	 * Takes in a User and returns a list of all RideRequests associated with
	 * the User.
	 *
	 * @param User
	 *            u the active user.
	 * 
	 * @return List<RideRequest> a list of all requests associated with the
	 *         User.
	 */
	public List<RideRequest> getRequestsForUser(User u) {
		return rideReqRepo.findByUser(u);
	}

	/**
	 * Takes in a User and returns a list of completed Rides associated with the
	 * User.
	 *
	 * @param User
	 *            u the active user.
	 * 
	 * @return List<Ride> a list of completed Rides.
	 */
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

	/**
	 * Takes in a User and returns a list of completed Rides associated with the
	 * User.
	 *
	 * @param User
	 *            u the active user.
	 * 
	 * @return List<Ride> a list of completed Rides.
	 */
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

	/**
	 * Takes in a User object and uses said object to retrieve a list of all
	 * AvailableRide objects.
	 *
	 * @param User
	 *            u the active user.
	 * @return List<AvailableRide> a list of all AvailableRide objects
	 *         associated with the User.
	 */
	public List<AvailableRide> getOffersForUser(User u) {
		return availRideRepo.findByCarUser(u);
	}

	/**
	 * Takes in an AvailableRide object and persists it to the database.
	 *
	 * @param AvailableRide
	 *            offer the AvailableRide to persist.
	 * @return boolean returns true on success, false on failure.
	 */
	public boolean addOffer(AvailableRide offer) {
		AvailableRide temp = availRideRepo.saveAndFlush(offer);
		if (temp == null) {
			return false;
		}
		return true;
	}

	/**
	 * Takes in the AvailableRide id and User to create a Ride assigned to the
	 * Request and Offer.
	 *
	 * @param long
	 *            id the id of the AvailableRide to assign the Request to.
	 * 
	 * @param User
	 *            u the active user.
	 * 
	 * @return boolean returns true on success, false on failure.
	 */
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

	/**
	 * Takes in an AvailableRide id and deletes ALL Rides associated with it.
	 * Sets the RequestStatus of ALL RideRequest objects associated to 'OPEN'
	 * and deletes the AvailableRide object.
	 *
	 * @param long
	 *            id The id of the Ride to cancel.
	 * @return boolean true on success, false on failure.
	 */
	public boolean cancelOffer(long id, User u) {
		try {
			List<Ride> rides = rideRepo.findAllByAvailRideAvailRideId(id);
			AvailableRide availRide = rides.get(0).getAvailRide();

			for (Ride r : rides) {
				RideRequest temp = r.getRequest();
				temp.setStatus(RequestStatus.OPEN); // reopen request
				rideReqRepo.save(temp); // update Request

				rideRepo.delete(r);
			}

			availRideRepo.delete(availRide);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Takes in the main poi's id and returns all open requests starting at said
	 * id. List is ordered by closest to farthest POI and within each of those,
	 * by date.
	 *
	 * @param int
	 *            id The id of the main POI(Point of Interest).
	 * @return List<AvailableRide> A list of Available Rides.
	 */
	public List<AvailableRide> getOpenOffers(int poiId) {
		List<AvailableRide> openOffers = availRideRepo.findAllByIsOpenTrue();

		Collections.sort(openOffers); // Sorting by date.

		// Sorting by closest to farthest POI
		PointOfInterest temp = poiService.getAll().get(poiId);
		openOffers = sortAvailableByPOI(openOffers, temp);

		return openOffers;
	}

	/**
	 * Takes in a User object and uses said object to retrieve a list of
	 * Available Ride objects.
	 *
	 * @param User
	 *            u the active user.
	 * @return List<AvailableRide> A list of Available Rides.
	 */
	public List<AvailableRide> getOpenOffersForUser(User u) {

		List<AvailableRide> allOpenOffers = availRideRepo.findAllByIsOpenTrue();
		List<AvailableRide> openOffers = new ArrayList<AvailableRide>();

		// filter rides to get ride for user
		for (AvailableRide a : allOpenOffers) {
			if (a.getCar().getUser().getUserId() == u.getUserId()) {
				openOffers.add(a);
			}
		}
		return openOffers;
	}

	/**
	 * Takes in a User object and uses said object to retrieve a list of
	 * non-completed Ride objects.
	 *
	 * @param User
	 *            u the active user.
	 * @return List<Ride> A list of Rides.
	 */
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

	/**
	 * Takes in a User object and uses said object to retrieve a list of
	 * completed Ride objects.
	 *
	 * @param User
	 *            u the active user.
	 * @return List<Ride> A list of Rides.
	 */
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

	/**
	 * Returns a list of RideRequest Objects in order from closest destination
	 * point to farthest away.
	 *
	 * @param List<RideRequest>
	 *            reqs a list of all open RideRequests
	 * @param PointOfInterest
	 *            mpoi the user's main POI, used as a starting(pickup) point for
	 *            all calculations.
	 * @return list of PointOfInterest objects.
	 */
	public List<RideRequest> sortRequestsByPOI(List<RideRequest> reqs, PointOfInterest mpoi) {
		List<RideRequest> temp = new ArrayList<RideRequest>();
		List<PointOfInterest> pois = poiService.getAll();

		int[] poisByDistance = calculateDistance(pois, mpoi);
		int count = 0;

		for (int i : poisByDistance) {
			for (int k = 0; k < reqs.size(); k++) {
				if (reqs.get(k).getDropOffLocation().getPoiId() == i + 1
						&& mpoi.getPoiId() == reqs.get(k).getPickupLocation().getPoiId()) {
					temp.add(reqs.get(k));
					reqs.remove(k--);
				}
			}
		}

		return temp;
	}

	/**
	 * Returns a list of AvailableRide Objects in order from closest destination
	 * point to farthest away.
	 *
	 * @param List<AvailableRide>
	 *            reqs a list of all open AvailableRide
	 * @param PointOfInterest
	 *            mpoi the user's main POI, used as a starting(pickup) point for
	 *            all calculations.
	 * @return list of PointOfInterest objects.
	 */
	public List<AvailableRide> sortAvailableByPOI(List<AvailableRide> reqs, PointOfInterest poi) {
		List<AvailableRide> temp = new ArrayList<AvailableRide>();
		List<PointOfInterest> pois = poiService.getAll();

		int[] poisByDistance = calculateDistance(pois, poi);
		for (int i : poisByDistance) {
			for (AvailableRide rq : reqs) {
				if (rq.getPickupPOI().getPoiId() == i) {
					temp.add(rq);
				}
			}
		}
		return temp;
	}

	/**
	 * Returns a list of PointOfInterest Objects in order from closest to
	 * farthest away, excluding the main PointOfInterest.
	 *
	 * @param List<PointOfInterest>
	 *            pois a list of all available POIs
	 * @param PointOfInterest
	 *            mpoi the user's main POI
	 * @return list of PointOfInterest objects.
	 */
	private int[] calculateDistance(List<PointOfInterest> pois, PointOfInterest mpoi) {
		double mLat = Math.abs(mpoi.getLatitude());
		double mLong = Math.abs(mpoi.getLongitude());
		Map<Double, Integer> map = new TreeMap();

		// Calculating distance: sqrt( (|x1|-|x2|) + (|y1|-|y2|)^2 )
		// distance is then stored in a Treemap which naturally orders.
		for (int i = 0; i < pois.size(); i++) {
			// skipping the main POI.
			if (mpoi.getPoiId() == pois.get(i).getPoiId()) {
				continue;
			}
			double poiLat = Math.abs(pois.get(i).getLatitude());
			double poiLong = Math.abs(pois.get(i).getLongitude());

			double x = mLong - poiLong;
			double y = mLat - poiLat;

			double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
			map.put(distance, i);
		}

		Set<?> set = map.entrySet();
		Iterator<?> iter = set.iterator();

		// -1 because it does not include the current poi
		int[] poiByDistance = new int[pois.size() - 1];

		int counter = pois.size() - 2;

		// creates an array of POI ids in order from closest to farthest away.
		while (iter.hasNext()) {
			Map.Entry me = (Map.Entry) iter.next();
			poiByDistance[counter--] = (Integer) me.getValue();
		}

		return poiByDistance;
	}

}
