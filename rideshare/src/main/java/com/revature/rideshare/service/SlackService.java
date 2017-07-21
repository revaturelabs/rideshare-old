package com.revature.rideshare.service;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
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
		
		// Creating the options and adding them to the list;
		ArrayList<PointOfInterest> pois = (ArrayList) poiService.getAll();
		for (PointOfInterest poi : pois) {
			Option o = new Option(poi.getPoiName(), poi.getPoiName());
			options.add(o);
		}
		
		// Creating the first set of actions
		Action action = new Action("POI", "Pick a destination", "select", options);
		actions.add(action);
		
		// Modifying options of the Action for String delimitation
		for (int i = 0; i < actions.size(); i++) {
			ArrayList<Option> actionOptions = actions.get(i).getOptions();
			for (Option option : actionOptions) {
				option.setValue("" + i + "-" + option.getValue());
			}
		}
		
		// Creating the second set of actions
		Action a1 = new Action("OKAY", "OK", "button", "okay");
		Action a2 = new Action("cancel", "CANCEL", "button", "cancel");
		actions2.add(action);
		actions2.add(a1);
		actions2.add(a2);
		
		// Creating the attachments
		Attachment attachment = new Attachment("From Destination", "Unable to decide", "newRideMessage", "#3AA3E3", "default", actions); 
		Attachment attachment2 = new Attachment("To Destination", "Unable to decide", "newRideMessage", "#3AA3E3", "default", actions2); 
		attachments.add(attachment);
		attachments.add(attachment2);
		
		SlackJSONBuilder rr = new SlackJSONBuilder(userId, "Ride request for " + date, "in_channel", attachments);
		
		String rideMessage = "";
		try {
			rideMessage = mapper.writeValueAsString(rr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rideMessage;
	}
	
	public boolean isMessageActionable(JsonNode payload) {
		String callbackId = payload.path("callback_id").asText();
		String currentMessage = payload.path("original_message").toString();
		String userId = payload.path("user").path("id").asText();
		String text = payload.path("original_message").path("text").asText();
		String date = text.split(" ")[text.split(" ").length - 1];
		
		switch(callbackId) {
			case "newRideMessage":
				String template = newRideMessage(userId, date);
				return compareMessages(currentMessage, template);
			default:
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
