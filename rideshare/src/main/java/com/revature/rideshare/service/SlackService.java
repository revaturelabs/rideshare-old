package com.revature.rideshare.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.revature.rideshare.dao.AvailableRideRepository;
import com.revature.rideshare.dao.RideRepository;
import com.revature.rideshare.dao.RideRequestRepository;
import com.revature.rideshare.domain.AvailableRide;
import com.revature.rideshare.domain.Car;
import com.revature.rideshare.domain.PointOfInterest;
import com.revature.rideshare.domain.RideRequest;
import com.revature.rideshare.domain.RideRequest.RequestStatus;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.json.Action;
import com.revature.rideshare.json.Attachment;
import com.revature.rideshare.json.Option;
import com.revature.rideshare.json.SlackJSONBuilder;

@Component("slackService")
@Transactional
public class SlackService{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RideRepository rideRepo;

	@Autowired
	private RideService rideService;

	@Autowired
	private CarService carService;

	@Autowired
	private UserService userService;

	@Autowired
	private RideRequestRepository rideReqRepo;

	@Autowired
	private AvailableRideRepository availableRideRepo;

	@Autowired
	private PointOfInterestService poiService;

	private static Set<String> dates = new HashSet<String>();

	/**
	 * This static block fills a set of dates with valid Strings in the format [yyyymmdd]
	 * for the next two years.
	 */
	static {
		int currentYear = (new Date()).getYear() + 1900;
		for (int year = currentYear; year < currentYear + 2; year++) {
			for (int month = 1; month <= 12; month++) {
				for (int day = 1; day <= daysInMonth(year, month); day++) {
					StringBuilder date = new StringBuilder();
					date.append(String.format("%04d", year));
					date.append(String.format("%02d", month));
					date.append(String.format("%02d", day));
					dates.add(date.toString());
				}
			}
		}
	}

	public void setCarService(CarService carService) {
		this.carService = carService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setRideService(RideService rideService) {
		this.rideService = rideService;
	}

	public void setRideRepo(RideRepository rideRepo) {
		this.rideRepo = rideRepo;
	}

	public void setRideReqRepo(RideRequestRepository rideReqRepo) {
		this.rideReqRepo = rideReqRepo;
	}

	public void setAvailableRideRepo(AvailableRideRepository availableRideRepo) {
		this.availableRideRepo = availableRideRepo;
	}

	public void setPoiService(PointOfInterestService poiService) {
		this.poiService = poiService;
	}

	/**
	 * Creates an interactive message that will be sent to the user.
	 * The message contains five attachments.
	 * 		Attachment 1: contains three drop down menus for hour(1 - 12), minutes(00, 15, 30, 45), and meridian(AM, PM
	 * 		Attachment 2: contains a drop down menu for from POI
	 * 		Attachment 3: contains a drop down menu for to POI
	 * 		Attachment 4: contains a drop down menu for number of seats
	 * 		Attachment 5: contains two buttons: OKAY and CANCEL
	 * @param userId
	 * @param date
	 * @return a JSON string that contains the interactive message for a new ride
	 */
	public String newRideMessage(String userId, String date) {
		ObjectMapper mapper = new ObjectMapper();

		// Creating the JSON string
		ArrayList<Action> actions = new ArrayList<Action>();
		ArrayList<Action> actions2 = new ArrayList<Action>();
		ArrayList<Attachment> attachments = new ArrayList<Attachment>();
		ArrayList<Option> options = new ArrayList<Option>();
		String callbackId = "newRideMessage";

		// Creating the attachments
		Attachment fromPOIAttachment = createPOIAttachment("From Destination", callbackId);
		Attachment toPOIAttachment = createPOIAttachment("To Destination", callbackId);
		Attachment seatsAttachment = createSeatsAttachment(callbackId);

		attachments.add(createTimeAttachment(callbackId));
		attachments.add(fromPOIAttachment);
		attachments.add(toPOIAttachment);
		attachments.add(seatsAttachment);
		attachments.add(createConfirmationButtonsAttachment(callbackId));


		SlackJSONBuilder rr = new SlackJSONBuilder(userId, "New ride for " + date, "in_channel", attachments);
		rr.addDelimiters();

		String rideMessage = "";
		try {
			rideMessage = mapper.writeValueAsString(rr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rideMessage;
	}


	/**
	 * Creates an interactive message that will be sent to the user.
	 * The message contains five attachments.
	 * 		Attachment 1: contains three drop down menus for hour(1 - 12), minutes(00, 15, 30, 45), and meridian(AM, PM
	 * 		Attachment 2: contains a drop down menu for from POI
	 * 		Attachment 3: contains a drop down menu for to POI
	 * 		Attachment 4: contains two buttons: OKAY and CANCEL
	 * @param userId
	 * @param date
	 * @return a JSON string that contains the interactive message for a new request
	 */
	public String newRequestMessage(String userId, String date) {
		ObjectMapper mapper = new ObjectMapper();

		// Creating the JSON string
		ArrayList<Action> actions = new ArrayList<Action>();
		ArrayList<Action> actions2 = new ArrayList<Action>();
		ArrayList<Attachment> attachments = new ArrayList<Attachment>();
		ArrayList<Option> options = new ArrayList<Option>();
		String callbackId = "newRequestMessage";

		// Creating the attachments
		Attachment fromPOIAttachment = createPOIAttachment("From Destination", callbackId);
		Attachment toPOIAttachment = createPOIAttachment("To Destination", callbackId);

		attachments.add(createTimeAttachment(callbackId));
		attachments.add(fromPOIAttachment);
		attachments.add(toPOIAttachment);
		attachments.add(createConfirmationButtonsAttachment(callbackId));

		SlackJSONBuilder rr = new SlackJSONBuilder(userId, "Ride request for " + date, "in_channel", attachments);
		rr.addDelimiters();

		String requestMessage = "";
		try {
			requestMessage = mapper.writeValueAsString(rr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return requestMessage;
	}

	/**
	 * Creates the Attachment that contains a drop down menu with the number of seats a ride can have.
	 * @param callbackId
	 * @return the seats attachment
	 */
	public Attachment createSeatsAttachment(String callbackId){
		ArrayList<Option> seatOptions = new ArrayList<Option>();
		ArrayList<Action> actions = new ArrayList<Action>();
		for(int i=1;i<5;i++){
			Option o = new Option(Integer.toString(i),Integer.toString(i));
			seatOptions.add(o);
		}
		Action seatsAction = new Action("Seats","# of seats","select",seatOptions);
		actions.add(seatsAction);
		Attachment seatsAttachment = new Attachment("Select # of Seats", "Unable to decide", callbackId, "#3AA3E3", "default", actions);
		return seatsAttachment;
	}

	/**
	 * Creates a ride in the database using the values that that the user inputted from slack
	 * @param payload
	 * @return a confirmation message that contains the values the user inputted.
	 */
	@SuppressWarnings("deprecation")
	public String createRideByMessage(JsonNode payload){
		String message = payload.path("original_message").toString();
		String userId = payload.path("user").path("id").asText();
		User user = userService.getUserBySlackId(userId);
		System.out.println(user);
		Car userCar = carService.getCarForUser(user);
		if(userCar!=null){
			System.out.println("user has a car");
			ObjectMapper mapper = new ObjectMapper();
			try {
				ArrayList<String> strings=new ArrayList<String>();
				SlackJSONBuilder slackMessage = mapper.readValue(message, SlackJSONBuilder.class);
				strings=getTextFields(slackMessage);
				String dateString=strings.get(0);
				String hour = strings.get(1);
				String minute = strings.get(2);
				String meridian = strings.get(3);
				String pickupName = strings.get(4);
				String dropoffName = strings.get(5);
				Date time = createRideDate(dateString,hour,minute,meridian);
				short seatsAvailable = Short.parseShort(strings.get(6));
				PointOfInterest pickupPOI = poiService.getPoi(pickupName);
				PointOfInterest dropoffPOI = poiService.getPoi(dropoffName);
				AvailableRide availableRide = new AvailableRide();
				availableRide.setCar(userCar);
				availableRide.setPickupPOI(pickupPOI);
				availableRide.setDropoffPOI(dropoffPOI);
				availableRide.setSeatsAvailable(seatsAvailable);
				availableRide.setOpen(true);
				availableRide.setTime(time);
				availableRide.setNotes("");
				System.out.println(availableRide);
				availableRideRepo.saveAndFlush(availableRide);
				String confirmationMessage = "Your ride for " + time.toString()
				+ " from " + pickupName + " to " + dropoffName +" with "+seatsAvailable+" seats  has been created";
				return confirmationMessage;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("user has no car,slackId is, "+userId+",we have ");
		System.out.println(user.getSlackId());
		return null;
	}


	/**
	 * Creates a request confirmation message that contains the values that the user selected
	 * and creates a ride request in the application.
	 * @param payload, the slack payload
	 * @return the confirmation message
	 */
	public String createRequestByMessage(JsonNode payload) {
		String userId = payload.path("user").path("id").asText();
		String message = payload.path("original_message").toString();
		ObjectMapper mapper = new ObjectMapper();
		User user = userService.getUserBySlackId(userId);

		System.out.println("User ID: " + userId);
		System.out.println("Message: " + message);

		try {
			ArrayList<String> values = new ArrayList<String>();
			SlackJSONBuilder slackMessage = mapper.readValue(message, SlackJSONBuilder.class);
			values = getTextFields(slackMessage);
			values.forEach(v -> System.out.println("Value: " + v));
			String date = values.get(0);
			String hour = values.get(1);
			String minutes = values.get(2);
			String meridian = values.get(3);
			Date time = createRideDate(date, hour, minutes, meridian);
			String fromPOI = values.get(4);
			String toPOI = values.get(5);
			RideRequest rideRequest = new RideRequest();
			rideRequest.setUser(user);
			rideRequest.setStatus(RequestStatus.OPEN);
			rideRequest.setPickupLocation(poiService.getPoi(fromPOI));
			rideRequest.setDropOffLocation(poiService.getPoi(toPOI));
			rideRequest.setTime(time);
			rideService.addRequest(rideRequest);
			String confirmationMessage = "Your ride request for " + time.toString()
			+ " from " + fromPOI + " to " + toPOI + " has been created";
			return confirmationMessage;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates a date object
	 * @param dateString, a string that has the following format: "MM/DD"
	 * @param hour, the hour from 1 - 12
	 * @param minute, the minute from 1 - 59
	 * @param meridian, either AM or PM 
	 * @return a date object that is constructed with the values from the parameters
	 */
	@SuppressWarnings("deprecation")
	public Date createRideDate(String dateString,String hour,String minute,String meridian){
		int currentYear=new Date().getYear();
		int month = Integer.parseInt(dateString.split("/")[0]) - 1;
		int day = Integer.parseInt(dateString.split("/")[1]);
		int startHour = Integer.parseInt(hour);
		int startMinute = Integer.parseInt(minute);
		if(meridian.equals("AM")){
			if(startHour==12){
				startHour=0;
			}
		}else if(meridian.equals("PM")){
			if(startHour<12){
				startHour=startHour+12;
			}
		}
		Date time = new Date(currentYear,month,day,startHour,startMinute);
		return time;
	}

	/**
	 * Gets the "text" fields from each drop down menu in the message
	 * @param slackMessage
	 * @return an ArrayList that contains all the text fields
	 */
	public ArrayList<String> getTextFields(SlackJSONBuilder slackMessage){
		ArrayList<Attachment> attachments = slackMessage.getAttachments();
		ArrayList<String> strings = new ArrayList<String>();
		String[] dateSplit = slackMessage.getText().split(" ");
		strings.add(dateSplit[dateSplit.length-1]);
		for(Attachment attachment:attachments){
			ArrayList<Action> actions = attachment.getActions();
			for(Action action:actions){
				if(action.getType().equals("select")){
					strings.add(action.getText());
				}
			}
		}
		return strings;
	}

	/**
	 * Creates an Attachment that contains three drop down menus: hour, minutes, and meridian
	 * @param callbackId
	 * @return the time attachment
	 */
	public Attachment createTimeAttachment(String callbackId) {
		ArrayList<Option> hourOptions = new ArrayList<Option>();
		ArrayList<Option> minuteOptions = new ArrayList<Option>();
		ArrayList<Option> meridians = new ArrayList<Option>();
		ArrayList<Action> actions = new ArrayList<Action>();

		for (int i = 1; i <= 12; i++) {
			Option o = new Option(Integer.toString(i), Integer.toString(i));
			hourOptions.add(o);
		}

		for (int i = 0; i <= 45; i = i + 15) {
			Option o;
			if (i == 0)
				o = new Option(Integer.toString(i) + "0", Integer.toString(i) + "0");
			else
				o = new Option(Integer.toString(i), Integer.toString(i));
			minuteOptions.add(o);
		}

		Option am = new Option("AM", "AM");
		Option pm = new Option("PM", "PM");
		meridians.add(am);
		meridians.add(pm);

		Action hourAction = new Action("Hour", "hour", "select", hourOptions);
		Action minuteAction = new Action("Minute", "minute", "select", minuteOptions);
		Action meridianAction = new Action("Meridian", "AM/PM", "select", meridians);
		actions.add(hourAction);
		actions.add(minuteAction);
		actions.add(meridianAction);

		Attachment timeAttachment = new Attachment("Select a Time", "Unable to decide", callbackId, "#3AA3E3", "default", actions);

		return timeAttachment;
	}

	/**
	 * Creates an Attachment that contains a drop down menu that is populated with all of the POIs
	 * @param text, the text that will be displayed above the drop down menu
	 * @param callbackId
	 * @return the POI attachment
	 */
	public Attachment createPOIAttachment(String text, String callbackId) {
		ArrayList<Action> actions = new ArrayList<Action>();
		ArrayList<Option> poiOptions = new ArrayList<Option>();

		ArrayList<PointOfInterest> pois = (ArrayList) poiService.getAll();
		for (PointOfInterest poi : pois) {
			Option o = new Option(poi.getPoiName(), poi.getPoiName());
			poiOptions.add(o);
		}

		Action action = new Action("POI", "Pick a destination", "select",poiOptions);
		actions.add(action);

		Attachment attachment = new Attachment(text, "Unable to decide", "newRideMessage", "#3AA3E3", "default", actions);

		return attachment;
	}

	/**
	 * Creates an Attachment that contains an "OKAY" and a "CANCEL" button
	 * @param callbackId
	 * @return the confirmation/cancel attachment
	 */
	public Attachment createConfirmationButtonsAttachment(String callbackId) {
		ArrayList<Action> actions = new ArrayList<Action>();

		Action okayButton = new Action("OKAY", "OKAY", "button", "okay");
		Action cancelButton = new Action("cancel", "CANCEL", "button", "cancel");
		actions.add(okayButton);
		actions.add(cancelButton);

		Attachment buttonAttachment = new Attachment("Unable to display confirmation buttons", callbackId, "#3AA3E3", "default", actions);

		return buttonAttachment;
	}

	/**
	 * Process the values that the user submitted in the interactive message
	 * @param payload
	 * @return confirmation or error message that will be displayed to the user
	 */
	public String handleMessage(JsonNode payload){
		String callbackId=payload.path("callback_id").asText();
		ObjectMapper mapper = new ObjectMapper();
		String currentMessage = payload.path("original_message").toString();
		SlackJSONBuilder cMessage;
		try {
			cMessage = mapper.readValue(currentMessage, SlackJSONBuilder.class);
			ArrayList<String> strings= getTextFields(cMessage);
			if(strings.get(4).equals(strings.get(5))){
				return ("Invalid Selection: Cannot use matching origin and destination.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		switch(callbackId){
		case("newRideMessage"):

			return createRideByMessage(payload);

		case("newRequestMessage"):
			return createRequestByMessage(payload);		
		default:
			return "Message does not match any known callbackid";
		}
	}

	/**
	 * Checks to see if a message is ready to be processed
	 * @param payload
	 * @return true if message all fields in the message are filled, false otherwise
	 */
	public boolean isMessageActionable(JsonNode payload) {
		String callbackId = payload.path("callback_id").asText();
		String currentMessage = payload.path("original_message").toString();
		String userId = payload.path("user").path("id").asText();
		String text = payload.path("original_message").path("text").asText();
		String date = text.split(" ")[text.split(" ").length - 1];

		Method method;
		try{
			method = this.getClass().getMethod(callbackId, userId.getClass(),date.getClass());
			String template = (String) method.invoke(this, userId,date);
			return compareMessages(currentMessage, template);
		}catch(SecurityException|IllegalArgumentException|NoSuchMethodException|IllegalAccessException|InvocationTargetException ex){
			logger.error("Reflection call error",ex);
		}
		return false;
	}

	/**
	 * Compares the user's message with the original message template to see if all fields have been filled.
	 * @param currentMessage
	 * @param template
	 * @return true if all fields filled, false otherwise
	 */
	public boolean compareMessages(String currentMessage, String template) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			//JsonNode cMessage = mapper.readTree(currentMessage);
			SlackJSONBuilder cMessage = mapper.readValue(currentMessage, SlackJSONBuilder.class);
			SlackJSONBuilder tMessage = mapper.readValue(template, SlackJSONBuilder.class);

			//System.out.println(cMessage.getAttachments());
			//System.out.println(tMessage.getAttachments());

			ArrayList<Attachment> cAttachments = cMessage.getAttachments();
			ArrayList<Attachment> tAttachments = tMessage.getAttachments();
			for (int i = 0; i < cAttachments.size(); i++) {
				ArrayList<Action> cActions = cAttachments.get(i).getActions();
				ArrayList<Action> tActions = tAttachments.get(i).getActions();
				for (int j = 0; j < cActions.size(); j++) {
					String type = cActions.get(j).getType();
					if (type.equals("select")) {
						if (cActions.get(j).getText().equals(tActions.get(j).getText())) {
							//System.out.println(cActions.get(j).getText() + " = " +  tActions.get(j).getText());
							return false;
						}
					}
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Checks if a date is a valid date.  A valid date is one that is not prior to today's date
	 * @param date, the date that is being validated
	 * @return true if the date is valid, false otherwise
	 */
	@SuppressWarnings("deprecation")
	public boolean acceptDate(String date) {
		String[] dateArray = date.split("/");
		String monthString = dateArray[0];
		String dayString = dateArray[1];
		String yearString = "" + ((new Date()).getYear() + 1900);

		if (isValidDate(yearString + monthString + dayString)) {
			// Create the user date in the format: yyyymmdd
			String userDate = yearString + monthString + dayString;

			// Create today's date in the format: yyyymmdd
			Date today = new Date();
			String todayMonth = "";
			if (today.getMonth() < 10)
				todayMonth = "0" + (today.getMonth() + 1);
			else
				todayMonth = "" + (today.getMonth() + 1);
			String todayDay = "" + today.getDate();;
			String todayYear = "" + ((new Date()).getYear() + 1900);
			String todayDate = todayYear + todayMonth + todayDay;

			// Return true if the userDate is either >= today's date
			if (userDate.compareTo(todayDate) >= 0)
				return true;
		}
		return false;
	}

	/**
	 * Gets the numbers of days in the specified month
	 * @param year
	 * @param month
	 * @return numbers of days in specified month
	 */
	private static int daysInMonth(int year, int month) {
		int daysInMonth;
		switch (month) {
		case 1: // fall through
		case 3: // fall through
		case 5: // fall through
		case 7: // fall through
		case 8: // fall through
		case 10: // fall through
		case 12:
			daysInMonth = 31;
			break;
		case 2:
			if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
				daysInMonth = 29;
			} else {
				daysInMonth = 28;
			}
			break;
		default:
			// returns 30 even for nonexistant months 
			daysInMonth = 30;
		}
		return daysInMonth;
	}

	/**
	 * Check to see if the date that the user inputed is a valid date
	 * @param dateString
	 * @return true if the date is valid, false otherwise
	 */
	public static boolean isValidDate(String dateString) {
		return dates.contains(dateString);
	}

	/**
	 * Checks if the time the user chose has already passed
	 * @param payload
	 * @return true if time has passed, false otherwise
	 */
	public boolean isPreviousTime(JsonNode payload) {
		String message = payload.path("original_message").toString();
		ObjectMapper mapper = new ObjectMapper();

		try {
			ArrayList<String> strings = new ArrayList<String>();
			SlackJSONBuilder slackMessage = mapper.readValue(message, SlackJSONBuilder.class);

			// This string array contains six elements:
			// 		date (mm/dd), hour, minute, meridian, from POI, and to POI
			strings = getTextFields(slackMessage);
			String[] dateArray = strings.get(0).split("/");
			Date userDate = createRideDate(strings.get(0), strings.get(1), strings.get(2), strings.get(3));
			if (isToday(userDate)) {
				if (timeHasPassed(userDate))
					return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Check if the date is the current date
	 * @param month
	 * @param day
	 * @return true if the date is the current date, false otherwise
	 */
	private boolean isToday(Date userDate) {
		Date today = new Date();

		if (userDate.getMonth() == today.getMonth() && userDate.getDate() == today.getDate())
			return true;
		else
			return false;
	}

	/**
	 * Check if the time that the user chose has already passsed
	 * @param userDate
	 * @return true if time has passed, false otherwise
	 */
	private boolean timeHasPassed(Date userDate){
		Date today = new Date();

		if (userDate.getHours() < today.getHours())
			return true;
		else if (userDate.getHours() == today.getHours() && userDate.getMinutes() < today.getMinutes())
			return true;
		return false;
	}
	
	/**
	 * Checks if the slackId is in the database
	 * @param slackId
	 * @return a User object if slackId exist, otherwise null
	 */
	public User isValidUser(String slackId) {
		return userService.getUserBySlackId(slackId);
	}
}
