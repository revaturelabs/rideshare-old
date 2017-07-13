package com.revature.rideshare.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.domain.PointOfInterest;
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

	@PostMapping("/addPoi")
	public void addPoi(@RequestBody PointOfInterest poi) {
		poiService.addPoi(poi);
	}

	@PostMapping("/removePoi")
	public void removePoi(@RequestBody PointOfInterest poi) {
		poiService.removePoi(poi);
	}

	@PostMapping("/updatePoi")
	public void updatePoi(@RequestBody PointOfInterest poi) {
		poiService.updatePoi(poi);
	}
}
