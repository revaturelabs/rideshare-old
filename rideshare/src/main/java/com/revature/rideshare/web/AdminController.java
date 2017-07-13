package com.revature.rideshare.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.domain.Car;
import com.revature.rideshare.domain.PointOfInterest;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.CarService;
import com.revature.rideshare.service.PointOfInterestService;
import com.revature.rideshare.service.RideService;
import com.revature.rideshare.service.UserService;

@RestController
@RequestMapping("admin")
public class AdminController {

	@Autowired
	private RideService rideService;

	@Autowired
	private UserService userService;

	@Autowired
	private CarService carService;

	@Autowired
	private PointOfInterestService poiService;

	@GetMapping("/admin/cars")
	public List<Car> getAllCars() {
		List<Car> cars = new ArrayList<Car>();
		cars = carService.getAll();
		System.out.println(cars);
		return carService.getAll();
	}

	@GetMapping("/admin/users")
	public List<User> getAllUsers() {
		return userService.getAll();
	}

	@PostMapping("/admin/updateStatus/{id}")
	public void updateStatus(@PathVariable(value = "id") long id) {
		User user = userService.getUser(id);
		userService.updateUser(user);
	}

	@PostMapping("/admin/removeUser/{id}")
	public void removeUser(@PathVariable(value = "id") long id) {
		User user = userService.getUser(id);
		userService.removeUser(user);
	}

	@PostMapping("/admin/addPOI")
	public void addPoi(@RequestBody PointOfInterest poi) {
		poiService.addPoi(poi);
	}

	@PostMapping("/admin/removePOI")
	public void removePoi(@RequestBody PointOfInterest poi) {
		poiService.removePoi(poi);
	}
}
