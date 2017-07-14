package com.revature.rideshare.web;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.domain.PointOfInterest;
import com.revature.rideshare.domain.RideRequest;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.PointOfInterestService;
import com.revature.rideshare.service.RideService;
import com.revature.rideshare.service.UserService;

@RestController
@RequestMapping("slack")
public class SlackController {

	@Autowired
	private RideService rideService;
	@Autowired
	private UserService userService;
	@Autowired
	private PointOfInterestService poiService;

	public void setRideService(RideService rideService) {
		this.rideService = rideService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	// REQUESTS
	@PostMapping("/request/add")
	public void addRequest(@RequestParam(name = "user_id") String userId,
			@RequestParam(name = "command") String command, @RequestParam(name = "text") String text) {
		// /request YYYY/MM/DD tt:tt pickupid dropoffid
		User u = userService.getUserBySlackId(userId);
		RideRequest request = new RideRequest();
		// Split the input by a space
		String delim = " ";
		String[] tokens = text.split(delim);
		// Split the variables for the LocalDateTime Object
		String[] dateTokens = tokens[0].split("/");
		String[] timeTokens = tokens[1].split(":");
		String checkPoi = tokens[2] + tokens[3];
		if (checkPoi.equalsIgnoreCase(("to work")) || tokens[2].equalsIgnoreCase("W")) {
			request.setPickupLocation(u.getMainPOI());
			request.setDropOffLocation(u.getWorkPOI());
		} else if (checkPoi.equalsIgnoreCase("to home") || tokens[2].equalsIgnoreCase("H")) {
			request.setPickupLocation(u.getWorkPOI());
			request.setDropOffLocation(u.getMainPOI());
		} else {
			int pickupLocationId = Integer.parseInt(tokens[2]);
			int dropoffLocationId = Integer.parseInt(tokens[3]);
			PointOfInterest pickupLocation = poiService.getPoi(pickupLocationId);
			PointOfInterest dropoffLocation = poiService.getPoi(dropoffLocationId);
			request.setPickupLocation(pickupLocation);
			request.setDropOffLocation(dropoffLocation);
		}
		Date rideDate = new Date(Integer.parseInt(dateTokens[0]) - 1900, Integer.parseInt(dateTokens[1]),
				Integer.parseInt(dateTokens[2]), Integer.parseInt(timeTokens[0]), Integer.parseInt(timeTokens[1]));

		request.setUser(u);
		// request.setTime(rideDate);

		rideService.addRequest(request);
	}

}