package com.revature.rideshare.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;

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
import com.revature.rideshare.domain.Ride;
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
				availableRide.setOpen(false);
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
			String confirmationMessage = "Your ride request for " + time.toString()
										+ " from " + fromPOI + " to " + toPOI + " has been created";
			return confirmationMessage;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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

	public Attachment createConfirmationButtonsAttachment(String callbackId) {
		ArrayList<Action> actions = new ArrayList<Action>();

		Action okayButton = new Action("OKAY", "OK", "button", "okay");
		Action cancelButton = new Action("cancel", "CANCEL", "button", "cancel");
		actions.add(okayButton);
		actions.add(cancelButton);

		Attachment buttonAttachment = new Attachment("Unable to display confirmation buttons", callbackId, "#3AA3E3", "default", actions);

		return buttonAttachment;
	}

	public String handleMessage(JsonNode payload){
		String callbackId=payload.path("callback_id").asText();
		switch(callbackId){
		case("newRideMessage"):
			return createRideByMessage(payload);
		
		case("newRequestMessage"):
			return createRequestByMessage(payload);		
		default:
			return "Message does not match any known callbackid";
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
			method = this.getClass().getMethod(callbackId, userId.getClass(),date.getClass());
			String template = (String) method.invoke(this, userId,date);
			return compareMessages(currentMessage, template);
		}catch(SecurityException|IllegalArgumentException|NoSuchMethodException|IllegalAccessException|InvocationTargetException ex){
			logger.error("Reflection call error",ex);
		}
		return false;
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
