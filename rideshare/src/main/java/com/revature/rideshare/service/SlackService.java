package com.revature.rideshare.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
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
/**
 * Used for sending constructing/sending slack messages and parsing
 * incoming slack messages. 
 * <br><br>Uses SlackJSONBuilder structure to dynamically
 * create slack messages with nested options and actions in attachment groups.
 * <br><br>The attachments are then inserted into the SlackJSONBuilder which is converted
 * into the message format required by slack (in String format.)
 * <br><br>Message payloads are parsable version of slack's response to
 * a user's interaction with a slack message.
 * *<br><br>Message callbackIds should be the same as their creation name for 
 * reflection invocation purposes.
 * <br>If a message's template can't be built from its payload, the message
 * should have a String-arg constructor with the same name to return logic comparison 
 * <br>(see methods 
 * {@link SlackService#handleMessage},
 * {@link SlackService#isMessageActionable})
 * @since 7/22/2017
 * @author Mark Worth
 * @author Gian-Carlo Barreto
 * @author Dylan McBee
 */
@Component("slackService")
@Transactional
public class SlackService{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	@Autowired
	private RideService rideService;

	@Autowired
	private CarService carService;

	@Autowired
	private UserService userService;

	@Autowired
	private PointOfInterestService poiService;

	private static Set<String> dates = new HashSet<String>();

	/**
	 * This static block fills a set of dates with valid Strings in the format [yyyymmdd]
	 * for the next two years.
	 */
	static {
		@SuppressWarnings("deprecation")
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

	public void setPoiService(PointOfInterestService poiService) {
		this.poiService = poiService;
	}

	/**
	  * Creates an interactive message that will be sent to the user.<br>
	 * The message contains five attachments.<br>
	 * 		Attachment 1: contains three drop down menus for:
	 * 		<ul>	
	 * 			<li>hour(1 - 12)</li>  
	 * 			<li>minutes(00, 15, 30, 45)</li>
	 * 			<li>meridian(AM, PM)</li>
	 * 		</ul>
	 * 		Attachment 2: contains a drop down menu for origin POI.<br>
	 * 		Attachment 3: contains a drop down menu for destination POI.<br>
	 * 		Attachment 4: contains a drop down menu for number of seats.<br>
	 * 		Attachment 5: contains two buttons: OKAY and CANCEL<br>
	 * @param String userId
	 * @param String text
	 * @return a JSON string that contains the interactive message for a new ride
	 */
	public String newRideMessage(String userId, String text) {
		ObjectMapper mapper = new ObjectMapper();
		String date = getDateFromText(text);
		ArrayList<Attachment> attachments = new ArrayList<Attachment>();
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
	 * Creates an interactive message that will be sent to the user.<br>
	 * The message contains four attachments.<br>
	 * 		Attachment 1: contains three drop down menus for:
	 * 		<ul>	
	 * 			<li>hour(1 - 12)</li>  
	 * 			<li>minutes(00, 15, 30, 45)</li>
	 * 			<li>meridian(AM, PM)</li>
	 * 		</ul>
	 * 		Attachment 2: contains a drop down menu for origin POI.<br>
	 * 		Attachment 3: contains a drop down menu for destination POI.<br>
	 * 		Attachment 4: contains two buttons: OKAY and CANCEL<br>
	 * @param String userId
	 * @param String text
	 * @return String New slack message.
	 */
	public String newRequestMessage(String userId, String text) {
		ObjectMapper mapper = new ObjectMapper();
		String date = getDateFromText(text);
		ArrayList<Attachment> attachments = new ArrayList<Attachment>();
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
	 * Pulls date from user slash command text.
	 * @param String text
	 * @return String date (mm/dd format)
	 */
	public String getDateFromText(String text){
		return text.split(" ")[text.split(" ").length-1];
	}
	
	/**
	 * Creates an interactive message that will be sent to the user.<br>
	 * The message contains four attachments.<br>
	 * 		Attachment 1: contains two drop down menus for:
	 * 		<ul>	
	 * 			<li>Destination/origin selection</li>  
	 * 			<li>Destination/origin POI</li>
	 * 		</ul>
	 * 		Attachment 2: contains a three drop down menus for start time selection with:<br>
	 * 		<ul>	
	 * 			<li>hour(1 - 12)</li>  
	 * 			<li>minutes(00, 15, 30, 45)</li>
	 * 			<li>meridian(AM, PM)</li>
	 * 		</ul>
	 * 		Attachment 3: contains a drop down menu for end time selection with:<br>
	 * 		<ul>	
	 * 			<li>hour(1 - 12)</li>  
	 * 			<li>minutes(00, 15, 30, 45)</li>
	 * 			<li>meridian(AM, PM)</li>
	 * 		</ul>
	 * 		Attachment 4: contains two buttons: OKAY and CANCEL<br>
	 * @param String userId
	 * @param String text
	 * @return String New slack message.
	 */
	public String findRidesMessage(String userId,String text){
		ObjectMapper mapper = new ObjectMapper();
		String date = getDateFromText(text);
		ArrayList<Attachment> attachments = new ArrayList<Attachment>();
		String callbackId = "findRidesMessage";

		// Creating the attachments
		Attachment toFromPOIAttachment = createPoiSelectDestinationAttachment(callbackId);
		Attachment startTimeAttachment = createTimeAttachment(callbackId);
		Attachment endTimeAttachment = createTimeAttachment(callbackId);
		startTimeAttachment.setText("Select start time.");
		endTimeAttachment.setText("Set end time.");
		attachments.add(toFromPOIAttachment);
		attachments.add(startTimeAttachment);
		attachments.add(endTimeAttachment);
		attachments.add(createConfirmationButtonsAttachment(callbackId));
		SlackJSONBuilder rr = new SlackJSONBuilder(userId, "Ride request for " + date, "in_channel", attachments);
		rr.addDelimiters();

		String ridesMessage = "";
		try {
			ridesMessage = mapper.writeValueAsString(rr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ridesMessage;
	}
	/**
	 * --Not Implemented, placed here for naming convention clarity in future iterations.--
	 * <br>Lets a driver find request matching their parameters.
	 * @param String userId
	 * @param String date
	 * @return String New slack message.
	 */
	public String findRequestsMessage(String userId,String date){
		return null;
	}
	
	/**
	 * Creates the Attachment that contains a drop down menu with the number of seats a ride can have.
	 * @param String callbackId
	 * @return Attachment Seats attachment.
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
	 * @param JsonNode payload
	 * @return String Confirmation message.
	 */
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
				rideService.addOffer(availableRide);
				String confirmationMessage = "Your ride for " + time.toString()
				+ " from " + pickupName + " to " + dropoffName +" with "+seatsAvailable+" seats  has been created";
				return confirmationMessage;
			} catch (IOException e) {
				logger.error("Exception occurred in creating ride through slack integration.");
			}
		}
		System.out.println("user has no car,slackId is, "+userId+",we have ");
		System.out.println(user.getSlackId());
		return null;
	}


	/**
	 * Creates a request confirmation message that contains the values that the user selected
	 * and creates a ride request in the application.
	 * @param JsonNode payload
	 * @return String Confirmation message.
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
			logger.error("Exception occurred when adding request through slack integration.");
		}
		return null;
	}
	/**
	 * Returns a boolean to the {@link SlackService#isMessageActionable} method
	 * to determine if the fields of the message have been filled by the user.
	 * @param String message
	 * @return boolean Determines if all fields have been filled.
	 */
	public boolean foundRidesByMessage(String message){
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			SlackJSONBuilder cMessage = mapper.readValue(message, SlackJSONBuilder.class);
			if(cMessage.getAttachments().get(0).getActions().get(0).getText().equals("Select from the following rides")){
				return false;
			}else{
				return true;
			}
		} catch (IOException e) {
			logger.error("Exception occurred when parsing message fields in slack integration.");
		}
		return false;
	}
	/**
	 * Returns a message which lets a user select rides matching their criteria.
	 * @param JsonNode payload
	 * @return String Confirmation message to be propagated as a message to slack user.
	 */
	public String foundRidesByMessage(JsonNode payload){
		ObjectMapper mapper = new ObjectMapper();
		String userId=getUserId(payload);
		ArrayList<Attachment> attachments = new ArrayList<Attachment>();
		String callbackId = "foundRidesByMessage";
		ArrayList<String> strings = getTextFields(payload);
		String dateString = strings.get(0);
		String filter = strings.get(1);
		String poiName = strings.get(2);
		String startHour = strings.get(3);
		String startMinute = strings.get(4);
		String startMeridian = strings.get(5);
		String endHour = strings.get(6);
		String endMinute = strings.get(7);
		String endMeridian = strings.get(8);
		Date startTime = createRideDate(dateString,startHour,startMinute,startMeridian);
		Date endTime = createRideDate(dateString,endHour,endMinute,endMeridian);
		// Creating the attachments
		System.out.println("start time: "+startTime);
		System.out.println("end time: "+endTime);
		Attachment availableRideAttachment = createAvailableRidesAttachment(startTime,endTime,filter,poiName,callbackId);
		
		attachments.add(availableRideAttachment);
		attachments.add(createConfirmationButtonsAttachment(callbackId));
		if(attachments.get(0).getActions().get(0).getOptions().size()==0){
			return "{\"replace_original\":\"true\",\"text\":\""+"No rides matching that time were found."+"\"}";
		}
		SlackJSONBuilder rr = new SlackJSONBuilder(userId, "Matching rides for "+ dateString, "in_channel", attachments);
		rr.addDelimiters();

		String requestMessage = "";
		try {
			requestMessage = mapper.writeValueAsString(rr);
		} catch (IOException e) {
			logger.error("Exception occurred when adding request through slack integration.");
		}
		return requestMessage;
	}
	/**
	 *--Not Implemented, placed here for naming convention clarity in future iterations.--
	 *<br>Follows the flow after findRequestByMessage.
	 * @param payload
	 * @return String Confirmation message to be propagated as a message to slack user.
	 */
	public String foundRequestsByMessage(JsonNode payload){
		return "Message not implemented";
	}
	
	/**
	 * Creates a date object constructed with the values from the parameters.
	 * @param dateString, a string that has the following format: "MM/DD"
	 * @param String hour (1-12)
	 * @param String minute (00-59)
	 * @param String meridian (AM/PM)
	 * @return Date Representing combination of input parameters.
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
	 * Gets the text fields from each drop down menu in the message
	 * @param slackMessage
	 * @return ArrayList<String> of user selection strings(positions 1-end) and selected date(position 0.)
	 */
	public ArrayList<String> getTextFields(JsonNode payload){
		String message = payload.path("original_message").toString();
		ObjectMapper mapper = new ObjectMapper();
		try {
			ArrayList<String> values = new ArrayList<String>();
			SlackJSONBuilder slackMessage = mapper.readValue(message, SlackJSONBuilder.class);
			values = getTextFields(slackMessage);
			return values;
		} catch (IOException e) {
			logger.error("Exception occurred when checking user selections through slack integration.");
		}
		return null;
	}
	
	public String getUserId(JsonNode payload){
		return payload.path("user").path("id").asText();
	}
	/**
	 * Extracts responseUrl from a payload (used to send slack message to slack user.)
	 * @param payload
	 * @return String Url for response to a slack message.
	 */
	public String getMessageUrl(JsonNode payload){
		return payload.path("response_url").asText();
	}
	/**
	 * --Only for interactive messages--<br>
	 * Convert http request into a usable JsonNode.
	 * @param request
	 * @return JsonNode Payload which can be parsed for message values.
	 * @throws UnsupportedEncodingException
	 */
	public JsonNode convertMessageRequestToPayload(String request){
		ObjectMapper mapper = new ObjectMapper();
		try {
			request = URLDecoder.decode(request, "UTF-8");
			request = request.substring(8);
			JsonNode payload = mapper.readTree(request);
			System.out.println("payload is "+payload);
			return payload;
		} catch (IOException e) {
			logger.error("Payload conversion exception");
			return null;
		}
	}
	/**
	 * Retrieves message selections a user has made in a message.
	 * @param slackMessage
	 * @return ArrayList<String> of user selection strings(positions 1-end) and selected date(position 0.)
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
	 * @return Attachment Contains time selection drop downs.
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
	 * @param String text This is displayed above the drop down menu.
	 * @param String callbackId
	 * @return Attachment Contains POI drop downs.
	 */
	public Attachment createPOIAttachment(String text, String callbackId) {
		ArrayList<Action> actions = new ArrayList<Action>();
		ArrayList<Option> poiOptions = new ArrayList<Option>();

		ArrayList<PointOfInterest> pois = (ArrayList<PointOfInterest>) poiService.getAll();
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
	 * @param String callbackId
	 * @return Attachment Confirm/Cancel
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
	 * Creates attachment for passengers to select rides.
	 * @param Date starttime
	 * @param Date endtime
	 * @param String filter
	 * @param String poiName
	 * @param String callbackId
	 * @return Attachment Which lets user select from rides matching their criteria.
	 */
	@SuppressWarnings("deprecation")
	public Attachment createAvailableRidesAttachment(Date starttime, Date endtime,String filter,String poiName,String callbackId){
		ArrayList<Action> actions = new ArrayList<Action>();
		ArrayList<Option> options = new ArrayList<Option>();
		PointOfInterest poi = poiService.getPoi(poiName);
		String destinationText="";
		String alternateDestinationText="";
		ArrayList<AvailableRide> rides = rideService.getAvailableRidesByTime(starttime, endtime);
		if(filter.equals("To")){
			rides=rideService.filterAvailableRidesByDropoffPoi(rides, poi);
			destinationText=poi.getPoiName();
		}else if(filter.equals("From")){
			rides=rideService.filterAvailableRidesByPickupPoi(rides, poi);
			destinationText=poi.getPoiName();
		}
		for(AvailableRide ride:rides){
			if(ride.isOpen()){
				if(filter.equals("To")){
					alternateDestinationText=ride.getPickupPOI().getPoiName();
				}else if(filter.equals("From")){
					alternateDestinationText=ride.getDropoffPOI().getPoiName();
				}
					Date time = ride.getTime();
					String hours = ""+time.getHours();
					String minutes = ""+time.getMinutes();
					String meridian = "AM";
					if(minutes.equals("0")){
						minutes = minutes+"0";
					}
					if(time.getHours()>=12){
						meridian = "PM";
						if(time.getHours()>12){
							hours = ""+(time.getHours()-12);
						}
					}
					String timeText = hours + ":" + minutes + meridian;
					String text = timeText+" "+destinationText+">"+alternateDestinationText+" ID:"+ride.getAvailRideId();
					Option o = new Option(text,text);
					options.add(o);
			}
		}
		Action action = new Action("AvailableRides","Select from the following rides","select",options);
		actions.add(action);
		Attachment availableRidesAttachment = new Attachment("AvailableRides","Available Rides","Unable to display available rides", callbackId, "#3AA3E3", "default", actions);
		return availableRidesAttachment;
	}
	/**
	 * Creates attachment for POI selection.
	 * @param String callbackId
	 * @return Attachment Which lets user select their destination/origin (to/from option with POI.)
	 */
	public Attachment createPoiSelectDestinationAttachment(String callbackId){
		ArrayList<Action> actions = new ArrayList<Action>();
		ArrayList<Option> poiOptions = new ArrayList<Option>();
		ArrayList<Option> toFromOptions = new ArrayList<Option>();
		Option toOption = new Option("To","To");
		Option fromOption = new Option("From","From");
		toFromOptions.add(toOption);
		toFromOptions.add(fromOption);
		ArrayList<PointOfInterest> pois = (ArrayList<PointOfInterest>) poiService.getAll();
		for (PointOfInterest poi : pois) {
			Option o = new Option(poi.getPoiName(), poi.getPoiName());
			poiOptions.add(o);
		}
		Action toFromAction = new Action("To/From","To/From","select",toFromOptions);
		Action poiAction = new Action("POI", "Pick a destination", "select",poiOptions);
		actions.add(toFromAction);
		actions.add(poiAction);
		Attachment attachment = new Attachment("Select a destination or origin", "Unable to view destinations", "newRideMessage", "#3AA3E3", "default", actions);
		return attachment;
	}
	
	/**
	 * Process the values that the user submitted in the interactive message
	 * @param JsonNode payload
	 * @return String Confirmation message to be propagated as a message to slack user.
	 */
	public String handleMessage(JsonNode payload){
		String callbackId=payload.path("callback_id").asText();
		ObjectMapper mapper = new ObjectMapper();
		String currentMessage = payload.path("original_message").toString();
		SlackJSONBuilder cMessage;
		try {
			cMessage = mapper.readValue(currentMessage, SlackJSONBuilder.class);
			ArrayList<String> strings= getTextFields(cMessage);
			boolean isNewRequestOrRide=(callbackId.equals("newRideMessage")||callbackId.equals("newRequestMessage"));
			if(isNewRequestOrRide&&strings.get(4).equals(strings.get(5))){
				return ("Invalid Selection: Cannot use matching origin and destination.");
			}
			boolean isFindRequestOrRide=(callbackId.equals("findRidesMessage")||callbackId.equals("findRequestsMessage"));
			if(isFindRequestOrRide&&strings.get(6).equals(strings.get(7))){
				return("Invalid Selection: Cannot use matching origin and destination");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		switch(callbackId){
		case("newRideMessage"):
			return createRideByMessage(payload);
		case("newRequestMessage"):
			return createRequestByMessage(payload);		
		case("findRidesMessage"):
			return foundRidesByMessage(payload);
		case("findRequestsMessage"):
			return foundRequestsByMessage(payload);
		case("foundRequestsByMessage"):
			return addPassengersToRideByMessage(payload);
		case("foundRidesByMessage"):
			return addUserToRideByMessage(payload);
		default:
			return "Message does not match any known callbackid, callbackId is "+callbackId;
		}
	}
	/**
	 * --Not implemented, only here for naming clarity--
	 * Should be used for a driver to add passengers to their ride.
	 * @param JsonNode payload
	 * @return Confirmation message to be propagated as a message to slack user.
	 */
	private String addPassengersToRideByMessage(JsonNode payload) {
		return "Message not implemented";
	}
	/**
	 * Adds a user to a ride selected through slack integration messages.
	 * @param JsonNode payload
	 * @return String Confirmation message to be propagated as a message to slack user.
	 */
	private String addUserToRideByMessage(JsonNode payload) {
		SlackJSONBuilder message = this.convertPayloadToSlackJSONBuilder(payload);
		String userId = this.getUserId(payload);
		String rideInfo = message.getAttachments().get(0).getActions().get(0).getText();
		long rideId = Long.parseLong(rideInfo.split(":")[rideInfo.split(":").length-1]);
		User u = userService.getUserBySlackId(userId);
		AvailableRide ride = rideService.getRideById(rideId);
		Date time = ride.getTime();
		String fromPOI = ride.getPickupPOI().getPoiName();
		String toPOI = ride.getDropoffPOI().getPoiName();
		boolean addedUser = rideService.acceptOffer(rideId, u);
		String confirmationMessage;
		if(addedUser){
			confirmationMessage = "Your ride request for " + time.toString()
			+ " from " + fromPOI + " to " + toPOI + " has been created";
		}else{
			confirmationMessage = "There was a problem with your request. Please try again.";
		}
		return confirmationMessage;
	}
	/**
	 * Converts slack payload into Java object
	 * (Refer to {@link com.revature.rideshare.json.SlackJSONBuilder})
	 * @param JsonNode payload
	 * @return SlackJSONBuilder Usable Java object mapped to slack message.
	 */
	public SlackJSONBuilder convertPayloadToSlackJSONBuilder(JsonNode payload){
		ObjectMapper mapper = new ObjectMapper();
		String currentMessage = payload.path("original_message").toString();
		try {
			SlackJSONBuilder cMessage = mapper.readValue(currentMessage, SlackJSONBuilder.class);
			return cMessage;
		} catch (IOException e) {
			logger.error("Message extraction exception");
			return null;
		}
	}

	/**
	 * Checks to see if a message is ready to be processed<br>
	 * If the template for the message can be built from its payload, this will automatically check.<br><br>
	 * If not, the message must have a method matching its name, but 
	 * the argument must be the current message in String format.
	 * @param JsonNode payload
	 * @return boolean True if all fields in the message are filled, false otherwise.
	 */
	public boolean isMessageActionable(JsonNode payload) {
		String callbackId = payload.path("callback_id").asText();
		String currentMessage = payload.path("original_message").toString();
		String userId = payload.path("user").path("id").asText();
		String text = payload.path("original_message").path("text").asText();
		String date = text.split(" ")[text.split(" ").length - 1];
		
		Method method;
		try{
			String template="";
			if(templateCanBeBuiltFromPayload(callbackId)){
				method = this.getClass().getMethod(callbackId, userId.getClass(),date.getClass());
				template = (String) method.invoke(this, userId,date);
				return compareMessages(currentMessage, template);
			}else{//returns the boolean without template comparison(requires direct logic in named method[String-arg])
				method = this.getClass().getMethod(callbackId,currentMessage.getClass());
				return (Boolean) method.invoke(this,currentMessage);
			}
			
			
		}catch(SecurityException|IllegalArgumentException|NoSuchMethodException|IllegalAccessException|InvocationTargetException ex){
			logger.error("Reflection call error",ex);
		}
		return false;
	}
	/**
	 * Checks to see if a message is at the end of its message chain for propagating confirmation messages to slack user.
	 * @param String callbackId
	 * @return boolean True if message is able to send a confirmation message back to slack user. 
	 */
	public boolean isMessageEndOfBranch(String callbackId){
		return callbackId.equals("newRideMessage")||callbackId.equals("newRequestMessage")||callbackId.equals("foundRidesByMessage")||callbackId.equals("foundRequestsByMessage");
	}
	/**
	 * Checks to see if the template can be built for a particular message from its payload.
	 * @param String callbackId
	 * @return boolean True if messsage has a template that can be built off its current message.
	 */
	public boolean templateCanBeBuiltFromPayload(String callbackId){
		return !(callbackId.equals("foundRidesByMessage")||callbackId.equals("foundRequestsByMessage"));
	}

	/**
	 * Compares the user's message with the original message template to see if all fields have been filled.
	 * @param String currentMessage
	 * @param String template
	 * @return boolean True if all fields filled, false otherwise
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
	 * @param String date which is being validated
	 * @return boolean True if the date is valid, false otherwise
	 */
	@SuppressWarnings("deprecation")
	public boolean acceptDate(String date) {
		System.out.println(date);
		String[] dateArray = date.split("/");
		String monthString = dateArray[0];
		String dayString = dateArray[1];
		String yearString = "" + ((new Date()).getYear() + 1900);
		Date checkedDate = this.createRideDate(date, "11", "59", "PM");
		Date today = new Date();
		boolean isBeforeToday = today.before(checkedDate);
		System.out.println(""+checkedDate+today+isBeforeToday);
		return isBeforeToday;
//		if (isValidDate(yearString + monthString + dayString)) {
//			// Create the user date in the format: yyyymmdd
//			String userDate = yearString + monthString + dayString;
//			
//			// Create today's date in the format: yyyymmdd
//			Date today = new Date();
//			String todayMonth = "";
//			if (today.getMonth() < 10)
//				todayMonth = "0" + (today.getMonth() + 1);
//			else
//				todayMonth = "" + (today.getMonth() + 1);
//			String todayDay = "" + today.getDate();;
//			String todayYear = "" + ((new Date()).getYear() + 1900);
//			String todayDate = todayYear + todayMonth + todayDay;
//
//			// Return true if the userDate is either >= today's date
//			if (userDate.compareTo(todayDate) >= 0){
//				return true;
//			}
//		}
//		return false;
	}

	/**
	 * Returns the number of days in the specified month.
	 * @param int year
	 * @param int month
	 * @return int Number of days in specified month.
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
	 * @param String dateString
	 * @return boolean True if the date is valid, false otherwise
	 */
	public static boolean isValidDate(String dateString) {
		return dates.contains(dateString);
	}

	/**
	 * Checks if the time the user chose has already passed.
	 * @param JsonNode payload
	 * @return boolean True if time has passed, false otherwise
	 */
	public boolean isPreviousTime(JsonNode payload) {
		SlackJSONBuilder message = this.convertPayloadToSlackJSONBuilder(payload);
		ArrayList<String> strings = getTextFields(message);
		String callbackId = payload.path("callback_id").asText();
		System.out.println(callbackId);
		Date today = new Date();
		boolean isFoundRequestOrRide=(callbackId.equals("foundRidesByMessage")||callbackId.equals("foundRequestsByMessage"));
		if(isFoundRequestOrRide){
			//bypass the check for messages which have already undergone validation of time fields
			return false;
		}
		// This string array contains six elements:
		// 		date (mm/dd), hour, minute, meridian, from POI, and to POI
		boolean isFindRequestOrRide=(callbackId.equals("findRidesMessage")||callbackId.equals("findRequestsMessage"));
		
		if(!isFindRequestOrRide){
				Date userDate = createRideDate(strings.get(0), strings.get(1), strings.get(2), strings.get(3));
				if(userDate.before(today)){
					return true;
				}else{
					return false;
				}
//			if (isToday(userDate)) {
//				if (timeHasPassed(userDate))
//					return true;
//			}
		}else{
			Date startDate = createRideDate(strings.get(0),strings.get(3),strings.get(4),strings.get(5));
			Date endDate = createRideDate(strings.get(0),strings.get(6),strings.get(7),strings.get(8));
			System.out.println("Start\n"+startDate+"\nEnd\n"+endDate+"\nNow\n"+today);
			System.out.println(startDate.before(today));
			System.out.println(endDate.before(today));
			System.out.println(endDate.before(startDate));
			if(startDate.before(today)||endDate.before(today)||endDate.before(startDate)){
				return true;
			}else{
				return false;
			}
			
		}
	}

	/**
	 * Check if the date is the current date
	 * @param Date userDate
	 * @return boolean True if the date is the current date, false otherwise
	 */
	@SuppressWarnings("deprecation")
	private boolean isToday(Date userDate) {
		Date today = new Date();
		if (userDate.getMonth() == today.getMonth() && userDate.getDate() == today.getDate())
			return true;
		else
			return false;
	}

	/**
	 * Check if the time that the user chose has already passed.
	 * @param Date userDate
	 * @return boolean True if time has passed, false otherwise.
	 */
	@SuppressWarnings("deprecation")
	private boolean timeHasPassed(Date userDate){
		Date today = new Date();

		if (userDate.getHours() < today.getHours())
			return true;
		else if (userDate.getHours() == today.getHours() && userDate.getMinutes() < today.getMinutes())
			return true;
		return false;
	}
	
	/**
	 * Checks if the user is in the database.
	 * @param String slackId
	 * @return User If user exists with slackId, otherwise null.
	 */
	public User isValidUser(String slackId) {
		return userService.getUserBySlackId(slackId);
	}
	public String isValidUserAndDate(String slackId,String text){
		//split the text parameters by space.
		String[] params = text.split(" ");
		String date = params[0];
		if(isValidUser(slackId)!=null){
			if(acceptDate(date)){
				return "ok";
			}else{
			return "That date has passed. Please select a date of today or later.";
			}
		}else{
			return "You have not permitted the slack application in slack or don't exist in our database."
					+ " Please log in to the application and permit our application to use slash commands.";
		}
	}
}