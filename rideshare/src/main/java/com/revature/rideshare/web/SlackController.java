package com.revature.rideshare.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.revature.rideshare.domain.PointOfInterest;
import com.revature.rideshare.json.Action;
import com.revature.rideshare.json.Attachment;
import com.revature.rideshare.json.Option;
import com.revature.rideshare.json.SlackJSONBuilder;
import com.revature.rideshare.service.PointOfInterestService;
import com.revature.rideshare.service.SlackService;

@RestController
@RequestMapping("slack")
public class SlackController {


	/**
	 * creates an instance of the poiService
	 */
	@Autowired
	private PointOfInterestService poiService;

	/**
	 * Uses a factory and Reflection to build a logger aspect for logging what happens with our database.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * Creates an instance of the slackService
	 */
	@Autowired
	private SlackService slackService;

	//set the slack service
	public void setSlackService(SlackService slackService) {
		this.slackService = slackService;
	}

	//set the poi service
	public void setPoiService(PointOfInterestService poiService) {
		this.poiService = poiService;
	}


	/**
	 * This method will decode a request to make a ride, construct a ride message with the necessary fields
	 * to make a ride, and send back the options to the user on slack.
	 *
	 * @param userId
	 * @param responseUrl
	 * @param text
	 * @param request
	 * @throws UnsupportedEncodingException
	 */
	@PostMapping("/newride")
	public void sendRideMessage(@RequestParam(name = "user_id") String userId, @RequestParam(name = "response_url") String responseUrl, @RequestParam String text, @RequestBody String request) throws UnsupportedEncodingException{
		request = URLDecoder.decode(request, "UTF-8");
		RestTemplate restTemplate = new RestTemplate();

		if (slackService.isValidUser(userId) != null) {
			String[] params = text.split(" ");
			String date = params[0];
			if (slackService.acceptDate(date)){
				String rideMessage = slackService.newRideMessage(userId, date);
		        restTemplate.postForLocation(responseUrl, rideMessage);
			}
			else {
				String requestError = "{\"text\":\"The date you have chosen is not a valid date\"}";
				restTemplate.postForLocation(responseUrl, requestError);
			}
		}
		else {
			String validationError = "{\"text\":\"You are not authorized to use this command\"}";
			restTemplate.postForLocation(responseUrl, validationError);
		}
	}

	/**
	 * This method will decode a sent request to ask for a ride, construct  a request message with
	 * the necessary fields to construct a ride, and send those options back to the user on slack.
	 *
	 * @param userId
	 * @param responseUrl
	 * @param text
	 * @param request
	 * @throws UnsupportedEncodingException
	 */
	@PostMapping("/newrequest")
	public void sendRequestMessage(@RequestParam(name = "user_id") String userId, @RequestParam(name = "response_url") String responseUrl, @RequestParam String text, @RequestBody String request) throws UnsupportedEncodingException{

		//Decodes the request from the user, UTF-8 encoding.
		request = URLDecoder.decode(request, "UTF-8");
		RestTemplate restTemplate = new RestTemplate();

		if (slackService.isValidUser(userId) != null) {
			//split the text parameters by space.
			String[] params = text.split(" ");
			String date = params[0];

			if (slackService.acceptDate(date)) {
				String requestMessage = slackService.newRequestMessage(userId, date);
					// posts the message to the user on slack.
		        restTemplate.postForLocation(responseUrl, requestMessage);
			}
			else {
				// posts the message to the user on slack.
		    String requestError = "{\"text\":\"The date you have chosen is not a valid date\"}";
				restTemplate.postForLocation(responseUrl, requestError);
			}
		}
		else {
			String validationError = "{\"text\":\"You are not authorized to use this command\"}";
			restTemplate.postForLocation(responseUrl, validationError);
		}
	}
	/**
	 * Checks to see when slack controller is being accessed.
	 */
	@GetMapping("/check")
	public void getCheck(){
		System.out.println("in slack controller");
	}


	/**
	 * The endpoint, keeps track of user interaction with the slack messages.
	 * @param request
	 * @throws UnsupportedEncodingException
	 */
	@PostMapping("/postcheck")
	public void postCheck(@RequestBody String request) throws UnsupportedEncodingException {
		// Decodes the request
		request = URLDecoder.decode(request, "UTF-8");
		request = request.substring(8);
		System.out.println(request);

		ObjectMapper mapper = new ObjectMapper();
		try {
			RestTemplate restTemplate = new RestTemplate();
			// Converts the string into a JSON object
			JsonNode payload = mapper.readTree(request);
			int attachId = payload.path("attachment_id").asInt() - 1;
			String type = payload.path("actions").path(0).path("type").asText();
			String callbackId = payload.path("callback_id").asText();
			//tracks when a user selects a dropdown option.
			if (type.equals("select")) {
				String selectedValue = payload.path("actions").path(0).path("selected_options").path(0).path("value").asText();
				String[] positionValue = selectedValue.split("-");
				int position = Integer.parseInt(positionValue[0]);
				String value = positionValue[1];

				JsonNode originalMessage = payload.path("original_message");
				((ObjectNode)originalMessage.path("attachments").path(attachId).path("actions").path(position)).put("text", value);


		        String messageurl = payload.path("response_url").asText();
		        restTemplate.postForLocation(messageurl, originalMessage.toString());
			}
			//tracks the user when using a button.
			else if (type.equals("button")){
				String value = payload.path("actions").path(0).path("value").asText();
				//System.out.println("Values: " + value);
				String messageurl = payload.path("response_url").asText();
				if (value.equals("okay")) {

					boolean acceptRequest = slackService.isMessageActionable(payload);

					//if request is accepted, send the user the confirmation message. Else, do nothing
					if (acceptRequest){
						if (slackService.isPreviousTime(payload)) {
							String error = "The time you have entered has already passed";
							restTemplate.postForLocation(messageurl,"{\"replace_original\":\"true\",\"text\":\"" + error + "\"}");
						}
						else {
							boolean isMessageEndOfBranch =slackService.isMessageEndOfBranch(callbackId);
							String confirmationMessage=slackService.handleMessage(payload);
							System.out.println(isMessageEndOfBranch);
							if(isMessageEndOfBranch){
								restTemplate.postForLocation(messageurl,"{\"replace_original\":\"true\",\"text\":\""+confirmationMessage+"\"}");
								System.out.println("Accept Request");
							}else{
								System.out.println("Confirmation Message:\n"+confirmationMessage);
								System.out.println("Payload:\n"+payload.toString());
								restTemplate.postForLocation(messageurl,confirmationMessage);
							}
						}
					}
					else {
						System.out.println("Reject Request");
					}
				}
				//if cancel is clicked, message sent saying the ride was canceled.
				else if (value.equals("cancel")) {
					System.out.println("Cancel Button clicked");
					restTemplate.postForLocation(messageurl, "{\"replace_original\":\"true\",\"text\":\"Your ride request has been cancelled\"}");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/testslack", method = RequestMethod.GET)
	public void sendMessage() {
		System.out.println("IN SLACK CONTROLLER");
		RestTemplate restTemplate = new RestTemplate();
        String messageurl="https://hooks.slack.com/services/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";
		ObjectMapper mapper = new ObjectMapper();


		// Creating the JSON string
		ArrayList<Action> actions = new ArrayList<Action>();
		ArrayList<Action> actions2 = new ArrayList<Action>();
		ArrayList<Attachment> attachments = new ArrayList<Attachment>();
		ArrayList<Option> options = new ArrayList<Option>();

		// Creating the options and adding them to the list;
		ArrayList<PointOfInterest> pois = (ArrayList<PointOfInterest>) poiService.getAll();
		for (PointOfInterest poi : pois) {
			Option o = new Option(poi.getPoiName(), poi.getPoiName().toLowerCase());
			options.add(o);
		}


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

		SlackJSONBuilder rr = new SlackJSONBuilder("@genesis", "Ride request", "in_channel", attachments);

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
