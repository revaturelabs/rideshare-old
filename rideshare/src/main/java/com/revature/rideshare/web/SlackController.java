package com.revature.rideshare.web;

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
    public void addRequest(@RequestParam(name="user_id") String userId, @RequestParam(name="command") String command, @RequestParam(name="text") String text){
        // /request MM/DD tt:tt "pickup" "dropoff"
    	User u = userService.getUserBySlackId(userId);
    	RideRequest request = new RideRequest();
    	String delim = " ";
    	String[] tokens = text.split(delim); 
    	int pickupLocationId = Integer.parseInt(tokens[2]);
    	//PointOfInterest pickupLocation = poiService.
    
    	
//    	try{
//    		
//    	}catch(IOException e){
//    		e.printStackTrace();
//    	}
    	request.setUser(u);

    	
    	rideService.addRequest(request);
    }
	
}
