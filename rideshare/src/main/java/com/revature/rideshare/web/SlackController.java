package com.revature.rideshare.web;

import java.io.IOException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.revature.rideshare.domain.AvailableRide;
import com.revature.rideshare.domain.Car;
import com.revature.rideshare.domain.PointOfInterest;
import com.revature.rideshare.domain.RideRequest;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.json.Action;
import com.revature.rideshare.json.Attachment;
import com.revature.rideshare.json.Option;
import com.revature.rideshare.json.RideRequestJSON;
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
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
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
	
	public void setPoiService(PointOfInterestService poiService) {
		this.poiService = poiService;
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
	
	@PostMapping("/newride")
	public void sendRideMessage(){
		RestTemplate restTemplate = new RestTemplate();
        String messageurl="https://hooks.slack.com/services/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";
		ObjectMapper mapper = new ObjectMapper();
		// Creating the JSON string
		ArrayList<Action> actions = new ArrayList<Action>();
		ArrayList<Action> actions2 = new ArrayList<Action>();
		ArrayList<Attachment> attachments = new ArrayList<Attachment>();
		ArrayList<Option> options = new ArrayList<Option>();
		
		// Creating the options and adding them to the list;
		ArrayList<PointOfInterest> pois = (ArrayList) poiService.getAll();
		for (PointOfInterest poi : pois) {
			Option o = new Option(poi.getPoiName(), poi.getPoiName().toLowerCase());
			options.add(o);
		}
//		Option o1 = new Option("Revature", "revature");
//		Option o2 = new Option("Camden", "camden");
//		Option o3 = new Option("ICON", "icon");
//		options.add(o1);
//		options.add(o2);
//		options.add(o3);
		
		// Creating the first set of actions
		Action action = new Action("POI", "Pick a destination", "select", options);
		actions.add(action);
		
		// Creating the second set of actions
		Action a1 = new Action("OKAY", "OK", "button", "okay");
		Action a2 = new Action("cancel", "CANCEL", "button", "cancel");
		actions2.add(action);
		actions2.add(a1);
		actions2.add(a2);
		
		// Creating the attachments
		Attachment attachment = new Attachment("From Destination", "Unable to decide", "message", "#3AA3E3", "default", actions); 
		Attachment attachment2 = new Attachment("To Destination", "Unable to decide", "message", "#3AA3E3", "default", actions2); 
		attachments.add(attachment);
		attachments.add(attachment2);
		
		RideRequestJSON rr = new RideRequestJSON("@gianbarreto1", "Ride request", "in_channel", attachments);
		
		String json = "";
		try {
			json = mapper.writeValueAsString(rr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
        restTemplate.postForLocation(messageurl, json);
	}
	
	@PostMapping("/newrequest")
	public void sendRequestMessage(){
		RestTemplate restTemplate = new RestTemplate();
        String messageurl="https://hooks.slack.com/services/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";
		ObjectMapper mapper = new ObjectMapper();
		
		
		// Creating the JSON string
		ArrayList<Action> actions = new ArrayList<Action>();
		ArrayList<Action> actions2 = new ArrayList<Action>();
		ArrayList<Attachment> attachments = new ArrayList<Attachment>();
		ArrayList<Option> options = new ArrayList<Option>();
		
		// Creating the options and adding them to the list;
		ArrayList<PointOfInterest> pois = (ArrayList) poiService.getAll();
		for (PointOfInterest poi : pois) {
			Option o = new Option(poi.getPoiName(), poi.getPoiName().toLowerCase());
			options.add(o);
		}
//		Option o1 = new Option("Revature", "revature");
//		Option o2 = new Option("Camden", "camden");
//		Option o3 = new Option("ICON", "icon");
//		options.add(o1);
//		options.add(o2);
//		options.add(o3);
		
		// Creating the first set of actions
		Action action = new Action("POI", "Pick a destination", "select", options);
		actions.add(action);
		
		// Creating the second set of actions
		Action a1 = new Action("OKAY", "OK", "button", "okay");
		Action a2 = new Action("cancel", "CANCEL", "button", "cancel");
		actions2.add(action);
		actions2.add(a1);
		actions2.add(a2);
		
		// Creating the attachments
		Attachment attachment = new Attachment("From Destination", "Unable to decide", "message", "#3AA3E3", "default", actions); 
		Attachment attachment2 = new Attachment("To Destination", "Unable to decide", "message", "#3AA3E3", "default", actions2); 
		attachments.add(attachment);
		attachments.add(attachment2);
		
		RideRequestJSON rr = new RideRequestJSON("@gianbarreto1", "Ride request", "in_channel", attachments);
		
		String json = "";
		try {
			json = mapper.writeValueAsString(rr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
        restTemplate.postForLocation(messageurl, json);
	}
	
	@GetMapping("/check")
	public void getCheck(){
		System.out.println("in slack controller");
	}
	@PostMapping("/postcheck")
	public void postCheck(){
		System.out.println("in post slack controller");
	}

	@RequestMapping(value = "/testslack", method = RequestMethod.GET)
	public void sendMessage() {
		System.out.println("IN SLACK CONTROLLER");
		RestTemplate restTemplate = new RestTemplate();
        String messageurl="https://hooks.slack.com/services/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";
		ObjectMapper mapper = new ObjectMapper();
		//PointOfInterestService service = new PointOfInterestService();

		// Creating the JSON string
		ArrayList<Action> actions = new ArrayList<Action>();
		ArrayList<Action> actions2 = new ArrayList<Action>();
		ArrayList<Attachment> attachments = new ArrayList<Attachment>();
		ArrayList<Option> options = new ArrayList<Option>();

		// Creating the options and adding them to the list;
		ArrayList<PointOfInterest> pois = (ArrayList) poiService.getAll();
		for (PointOfInterest poi : pois) {
			Option o = new Option(poi.getPoiName(), poi.getPoiName().toLowerCase());
			options.add(o);
		}
//		Option o1 = new Option("Revature", "revature");
//		Option o2 = new Option("Camden", "camden");
//		Option o3 = new Option("ICON", "icon");
//		options.add(o1);
//		options.add(o2);
//		options.add(o3);

		// Creating the first set of actions
		Action action = new Action("POI", "Pick a destination", "select", options);
		actions.add(action);

		// Creating the second set of actions
		Action a1 = new Action("OKAY", "OK", "button", "okay");
		Action a2 = new Action("cancel", "CANCEL", "button", "cancel");
		actions2.add(action);
		actions2.add(a1);
		actions2.add(a2);

		// Creating the attachments
		Attachment attachment = new Attachment("From Destination", "Unable to decide", "message", "#3AA3E3", "default", actions);
		Attachment attachment2 = new Attachment("To Destination", "Unable to decide", "message", "#3AA3E3", "default", actions2);
		attachments.add(attachment);
		attachments.add(attachment2);

		RideRequestJSON rr = new RideRequestJSON("@genesis", "Ride request", "in_channel", attachments);

		String json = "";
		try {
			json = mapper.writeValueAsString(rr);
		} catch (IOException e) {
			e.printStackTrace();
		}


        restTemplate.postForLocation(messageurl, json);
		System.out.println("MESSAGE SENT");
	}
}
