package com.revature.rideshare.json;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import com.revature.rideshare.domain.PointOfInterest;
import com.revature.rideshare.service.PointOfInterestService;

public class SlackMessageTest {
	public static void main(String[] args) {
		RestTemplate restTemplate = new RestTemplate();
        String messageurl="https://hooks.slack.com/services/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";
		ObjectMapper mapper = new ObjectMapper();
		PointOfInterestService service = new PointOfInterestService();
		
		// Creating the JSON string
		ArrayList<Action> actions = new ArrayList<Action>();
		ArrayList<Action> actions2 = new ArrayList<Action>();
		ArrayList<Attachment> attachments = new ArrayList<Attachment>();
		ArrayList<Option> options = new ArrayList<Option>();
		
		// Creating the options and adding them to the list;
		ArrayList<PointOfInterest> pois = (ArrayList) service.getAll();
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
}
