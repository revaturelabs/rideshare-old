package com.revature.rideshare.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	private PointOfInterestService poiService;
	
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
//		List<Ride> openRides = rideRepo.findAllByRequestNotNullAndAvailRideNull();
//		List<RideRequest> openReqs = new ArrayList<RideRequest>();
//		
//		
//		for (Ride r : openRides) {
//			openReqs.add(r.getRequest());
//		}
		
		/*Between these block comments is all test data, use above code when DB is usable.*/
		List<RideRequest> openReqs = new ArrayList<RideRequest>();
		List<PointOfInterest> pois = poiService.getAll();
		
//		Date time = new Date(1499691600000L);
//		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		String now = "2016-11-09 10:30";
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime time = LocalDateTime.parse(now, format);
		
		RideRequest req1 = new RideRequest();
		req1.setTime(time);
		req1.setRequestId(1);
		req1.setDropOffLocation(pois.get(1));

		now = "2016-11-09 08:20";
		time = LocalDateTime.parse(now,format);
		RideRequest req2 = new RideRequest();
		req2.setTime(time);
		req2.setRequestId(2);
		req2.setDropOffLocation(pois.get(1));
		
		now = "2016-11-09 09:00";
		time = LocalDateTime.parse(now,format);
		RideRequest req3 = new RideRequest();
		req3.setTime(time);
		req3.setRequestId(3);
		req3.setDropOffLocation(pois.get(1));
		
		now = "2016-11-09 10:00";
		time = LocalDateTime.parse(now,format);
		RideRequest req4 = new RideRequest();
		req4.setTime(time);
		req4.setRequestId(4);
		req4.setDropOffLocation(pois.get(4));
		
		now = "2016-11-09 09:00";
		time = LocalDateTime.parse(now,format);
		RideRequest req5 = new RideRequest();
		req5.setTime(time);
		req5.setRequestId(5);
		req5.setDropOffLocation(pois.get(4));
		
		now = "2016-11-09 08:40";
		time = LocalDateTime.parse(now,format);
		RideRequest req6 = new RideRequest();
		req6.setTime(time);
		req6.setRequestId(6);
		req6.setDropOffLocation(pois.get(4));
		
		openReqs.add(req1);
		openReqs.add(req2);
		openReqs.add(req3);
		openReqs.add(req4);
		openReqs.add(req5);
		openReqs.add(req6);
		/*Between these block comments is all test data, use above code when DB is usable.*/
		
		Collections.sort(openReqs);
		
		for(RideRequest r : openReqs){
			System.out.println(r.toString());
		}
		PointOfInterest temp = poiService.getAll().get(1);
		sortRequestsByPOI(openReqs, temp);
			
		return openReqs;
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
		List<AvailableRide> openOffers = availRideRepo.findByIsOpenTrue();
				
		Collections.sort(openOffers);
		
		for(AvailableRide r : openOffers){
			System.out.println(r.toString());
		}
		
		return openOffers;
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
	
	/**
	 * Returns a list of RideRequest Objects in order from closest destination point to farthest away.
	 *
	 * @param  List<RideRequest> reqs  a list of all open RideRequests
	 * @param  PointOfInterest mpoi  the user's main POI, used as a starting(pickup) point for all calculations.
	 * @return list of PointOfInterest objects.
	 */
	public List<RideRequest> sortRequestsByPOI(List<RideRequest> reqs, PointOfInterest poi) {
		List<RideRequest> temp = new ArrayList<RideRequest>();
		List<PointOfInterest> pois = poiService.getAll();
		
		int[] poisByDistance = calculateDistance(pois, poi);	
		System.out.println(reqs.get(1).toString());
		for(int i : poisByDistance){
			for(RideRequest rq : reqs){
				System.out.println("POI IDs: " + i + " --> dropOff ID: " + rq.getDropOffLocation().getPoiId());
				if(rq.getDropOffLocation().getPoiId() == i) {
					temp.add(rq);
				}
			}
		}
		
		System.out.println("----------LIST OF ALL REQUESTS SORTED BY POI AND TIME! :D?");
		for(RideRequest rq : temp){
			System.out.println(rq.toString());
		}
		return temp;
	}
	
	/**
	 * Returns a list of RideRequest Objects in order from closest destination point to farthest away.
	 *
	 * @param  List<RideRequest> reqs  a list of all open RideRequests
	 * @param  PointOfInterest mpoi  the user's main POI, used as a starting(pickup) point for all calculations.
	 * @return list of PointOfInterest objects.
	 */
	public List<AvailableRide> sortAvailableByPOI(List<AvailableRide> reqs, PointOfInterest poi) {
		List<AvailableRide> temp = new ArrayList<AvailableRide>();
		List<PointOfInterest> pois = poiService.getAll();
		
		int[] poisByDistance = calculateDistance(pois, poi);	
		System.out.println(reqs.get(1).toString());
		for(int i : poisByDistance){
			for(AvailableRide rq : reqs){
				System.out.println("POI IDs: " + i + " --> dropOff ID: " + rq.getDropoffPOI().getPoiId());
				if(rq.getDropoffPOI().getPoiId() == i) {
					temp.add(rq);
				}
			}
		}
		
		System.out.println("----------LIST OF ALL REQUESTS SORTED BY POI AND TIME! :D?");
		for(AvailableRide rq : temp){
			System.out.println(rq.toString());
		}
		return temp;
	}
	
	/**
	 * Returns a list of PointOfInterest Objects in order from closest to farthest away.
	 *
	 * @param  List<PointOfInterest> pois  a list of all available POIs
	 * @param  PointOfInterest mpoi  the user's main POI
	 * @return list of PointOfInterest objects.
	 */
	private int[] calculateDistance(List<PointOfInterest> pois, PointOfInterest mpoi){
		double mLat = Math.abs(mpoi.getLatitude());
		double mLong =  Math.abs(mpoi.getLongitude());
		Map<Double, Integer> map = new TreeMap();
		
		for(int i = 0; i < pois.size(); i++) {
			if(mpoi.getPoiId() != pois.get(i).getPoiId()){
				double poiLat = Math.abs(pois.get(i).getLatitude());
				double poiLong =  Math.abs(pois.get(i).getLongitude());
				
				double x = mLong - poiLong;
				double y = mLat - poiLat;
				
				double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
				map.put(distance, i);
			}
		}
		
		Set<?> set = map.entrySet();
		Iterator<?> iter = set.iterator();
		
		int[] poiByDistance = new int[pois.size()-1]; // -1 because it does not include the current poi
		int counter = pois.size()-2;
		while(iter.hasNext()) {
			Map.Entry me = (Map.Entry) iter.next();
			poiByDistance[counter--] = (Integer) me.getValue();		
		}

		return poiByDistance;
	}
}
