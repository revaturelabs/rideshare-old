package com.revature.rideshare.web;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.domain.AvailableRide;
import com.revature.rideshare.domain.Car;
import com.revature.rideshare.domain.RideRequest;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.CarService;
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
	@Autowired
	private CarService carService;

	public void setRideService(RideService rideService) {
		this.rideService = rideService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setCarService(CarService carService) {
		this.carService = carService;
	}

	// REQUESTS
	@PostMapping("/request/add")
	public void addRequest(@RequestParam(name = "user_id") String userId, @RequestParam(name = "text") String text) {
		// /request MM/DD tt:tt
		User u = userService.getUserBySlackId(userId);
		RideRequest request = new RideRequest();
		try {

			// Split the input by a space
			String delim = " ";
			String[] tokens = text.split(delim);
			// Split the variables for the Date Object
			String[] dateTokens = tokens[0].split("/");
			String[] timeTokens = tokens[1].split(":");
			@SuppressWarnings("deprecation")
			Date rideDate = new Date(LocalDate.now().getYear() - 1900, Integer.parseInt(dateTokens[0]) - 1,
					Integer.parseInt(dateTokens[1]), Integer.parseInt(timeTokens[0]), Integer.parseInt(timeTokens[1]));

			request.setUser(u);
			request.setTime(rideDate);
			rideService.addRequest(request);
		} catch (NumberFormatException nfe) {

		}catch (NullPointerException npe){
			
		}

		
	}

	// RIDES
	@PostMapping("/ride/add")
	public void addRide(@RequestParam(name = "user_id") String userId, @RequestParam(name= "text") String text){
		// /ride MM/DD hr:mm seats
		User u = userService.getUserBySlackId(userId);
		Car car = carService.getCarForUser(u);
		AvailableRide offer = new AvailableRide();
		try{
			String delim = " ";
			String[] tokens = text.split(delim);
			String[] dateTokens = tokens[0].split("/");
			String[] timeTokens = tokens[1].split(":");
			@SuppressWarnings("deprecation")
			Date offerDate = new Date(LocalDate.now().getYear() - 1900, Integer.parseInt(dateTokens[0]) - 1,
					Integer.parseInt(dateTokens[1]), Integer.parseInt(timeTokens[0]), Integer.parseInt(timeTokens[1]));
			short seats = Short.parseShort(tokens[2]);
			offer.setCar(car);
			offer.setSeatsAvailable(seats);
			offer.setTime(offerDate);
			rideService.addOffer(offer);
		}catch(NumberFormatException nfe){
			
		}catch(NullPointerException npe){
			
		}
		
	}

}