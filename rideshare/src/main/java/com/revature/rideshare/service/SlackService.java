package com.revature.rideshare.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;

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

	public String newRideMessage(String userId, String date) {
		ObjectMapper mapper = new ObjectMapper();

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


	public String newRequestMessage(String userId, String date) {
		ObjectMapper mapper = new ObjectMapper();
		
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

	public String findRidesMessage(String userId,String date){
		ObjectMapper mapper = new ObjectMapper();

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
	
	public String findRequestsMessage(String userId,String date){
		return null;
	}
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
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
		//TODO:remove test print statement 
		for(String string:strings){
			System.out.println("Form Input: "+string);
		}
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
			e.printStackTrace();
		}
		return requestMessage;
	}
	public String foundRequestsByMessage(JsonNode payload){
		return "Message not implemented";
	}
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
	
	public ArrayList<String> getTextFields(JsonNode payload){
		String message = payload.path("original_message").toString();
		ObjectMapper mapper = new ObjectMapper();
		try {
			ArrayList<String> values = new ArrayList<String>();
			SlackJSONBuilder slackMessage = mapper.readValue(message, SlackJSONBuilder.class);
			values = getTextFields(slackMessage);
			return values;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String getUserId(JsonNode payload){
		return payload.path("user").path("id").asText();
	}
	public String getMessageUrl(JsonNode payload){
		return payload.path("response_url").asText();
	}
	//only works for interactivemessage payloads
	public JsonNode convertMessageRequestToPayload(String request) throws UnsupportedEncodingException{
		request = URLDecoder.decode(request, "UTF-8");
		request = request.substring(8);
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode payload = mapper.readTree(request);
			System.out.println("payload is "+payload);
			return payload;
		} catch (IOException e) {
			logger.error("Payload conversion exception");
			return null;
		}
	}
	
	
	
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

	public Attachment createConfirmationButtonsAttachment(String callbackId) {
		ArrayList<Action> actions = new ArrayList<Action>();

		Action okayButton = new Action("OKAY", "OK", "button", "okay");
		Action cancelButton = new Action("cancel", "CANCEL", "button", "cancel");
		actions.add(okayButton);
		actions.add(cancelButton);

		Attachment buttonAttachment = new Attachment("Unable to display confirmation buttons", callbackId, "#3AA3E3", "default", actions);

		return buttonAttachment;
	}
	
	@SuppressWarnings("deprecation")
	public Attachment createAvailableRidesAttachment(Date starttime, Date endtime,String filter,String poiName,String callbackId){
		ArrayList<Action> actions = new ArrayList<Action>();
		ArrayList<Option> options = new ArrayList<Option>();
		PointOfInterest poi = poiService.getPoi(poiName);
		String destinationText="";
		String alternateDestinationText="";
		ArrayList<AvailableRide> rides = rideService.getAvailableRidesByTime(starttime, endtime);
		//TODO: remove this testing print statement
		System.out.println(""+rides.size()+" rides matching the specified time");
		for(AvailableRide ride:rides){
			System.out.println(ride.toString());
		}
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
			boolean isFindRequestOrRide=(callbackId.equals("findRideMessage")||callbackId.equals("findRequestMessage"));
			if(isFindRequestOrRide&&strings.get(6).equals(strings.get(7))){
				return("Invalid Selection: Cannot use matching origin and destination");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	
	//for drivers looking to add passengers
	private String addPassengersToRideByMessage(JsonNode payload) {
		return "Message not implemented";
	}
	
	//for passengers looking to join a ride
	private String addUserToRideByMessage(JsonNode payload) {
		SlackJSONBuilder message = this.convertPayloadToSlackJSONBuilder(payload);
		String userId = this.getUserId(payload);
		ArrayList<String> strings = this.getTextFields(payload);
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
			}else{//returns the boolean without template comparison(requires direct logic in named method[payload-args])
				method = this.getClass().getMethod(callbackId,currentMessage.getClass());
				return (Boolean) method.invoke(this,currentMessage);
			}
			
			
		}catch(SecurityException|IllegalArgumentException|NoSuchMethodException|IllegalAccessException|InvocationTargetException ex){
			logger.error("Reflection call error",ex);
		}
		return false;
	}

	public boolean isMessageEndOfBranch(String callbackId){
		return callbackId.equals("newRideMessage")||callbackId.equals("newRequestMessage")||callbackId.equals("foundRidesByMessage")||callbackId.equals("foundRequestsByMessage");
	}
	
	public boolean templateCanBeBuiltFromPayload(String callbackId){
		return !(callbackId.equals("foundRidesByMessage")||callbackId.equals("foundRequestsByMessage"));
	}
	public boolean compareMessages(String currentMessage, String template) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			//JsonNode cMessage = mapper.readTree(currentMessage);
			SlackJSONBuilder cMessage = mapper.readValue(currentMessage, SlackJSONBuilder.class);
			SlackJSONBuilder tMessage = mapper.readValue(template, SlackJSONBuilder.class);

			System.out.println(cMessage.getAttachments());
			System.out.println(tMessage.getAttachments());

			ArrayList<Attachment> cAttachments = cMessage.getAttachments();
			ArrayList<Attachment> tAttachments = tMessage.getAttachments();
			for (int i = 0; i < cAttachments.size(); i++) {
				ArrayList<Action> cActions = cAttachments.get(i).getActions();
				ArrayList<Action> tActions = tAttachments.get(i).getActions();
				for (int j = 0; j < cActions.size(); j++) {
					String type = cActions.get(j).getType();
					if (type.equals("select")) {
						if (cActions.get(j).getText().equals(tActions.get(j).getText())) {
							System.out.println(cActions.get(j).getText() + " = " +  tActions.get(j).getText());
							return false;
						}
					}
				}
			}
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
