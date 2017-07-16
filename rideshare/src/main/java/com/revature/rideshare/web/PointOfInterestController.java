package com.revature.rideshare.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.rideshare.domain.PointOfInterest;
import com.revature.rideshare.domain.PointOfInterestType;
import com.revature.rideshare.service.PointOfInterestService;

@RestController
@RequestMapping("poiController")
public class PointOfInterestController {

	@Autowired
	private PointOfInterestService poiService;

	@GetMapping
	public List<PointOfInterest> getAll() {
		return poiService.getAll();
	}

	@GetMapping("/type")
	public List<PointOfInterestType> getAllTypes() {
		return poiService.getAllTypes();
	}

	@PostMapping("/addPoi")
	public void addPoi(@RequestBody String jsonPoi) {
		System.out.println("in addPoi");
		PointOfInterest poi = getPoi(jsonPoi);
		System.out.println("\n\n\n" + poi.toString() + "\n\n\n");
		poiService.addPoi(poi);
	}

	@PostMapping("/removePoi")
	public void removePoi(@RequestBody String jsonPoi) {
		poiService.removePoi(getPoi(jsonPoi));
	}

	@PostMapping("/updatePoi")
	public void updatePoi(@RequestBody PointOfInterest poi) {
		poiService.updatePoi(poi);
	}

	public PointOfInterest getPoi(String jsonString) {
		ObjectMapper mapper = new ObjectMapper();

		System.out.println("in getPoi \n\n\n" + jsonString + "\n\n\n\n");
		try {
			return (PointOfInterest) mapper.readValue(jsonString, PointOfInterest.class);
		} catch (Exception e) {
			System.out.println("\n\n\n exception returns null \n\n\n");
			e.printStackTrace();
			return null;
		}
	}
}
