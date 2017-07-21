package com.revature.rideshare.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

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
import com.revature.rideshare.domain.PointOfInterest;
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
	private RideRequestRepository rideReqRepo;

	@Autowired
	private AvailableRideRepository availRideRepo;

	@Autowired
	private PointOfInterestService poiService;

	public void setRideRepo(RideRepository rideRepo) {
		this.rideRepo = rideRepo;
	}

	public void setRideReqRepo(RideRequestRepository rideReqRepo) {
		this.rideReqRepo = rideReqRepo;
	}

	public void setAvailRideRepo(AvailableRideRepository availRideRepo) {
		this.availRideRepo = availRideRepo;
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
		
		attachments.add(createTimeAttachment(callbackId));
		attachments.add(fromPOIAttachment);
		attachments.add(toPOIAttachment);
		attachments.add(createConfirmationButtonsAttachment(callbackId));
		
		SlackJSONBuilder rr = new SlackJSONBuilder(userId, "Ride request for " + date, "in_channel", attachments);
		rr.addDelimiters();
		
		String rideMessage = "";
		try {
			rideMessage = mapper.writeValueAsString(rr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rideMessage;
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
