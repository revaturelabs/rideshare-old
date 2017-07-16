package com.revature.rideshare.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.rideshare.domain.Car;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.CarService;
import com.revature.rideshare.service.UserService;

@RestController
@RequestMapping("car")
public class CarController {

	@Autowired
	private CarService carService;

	@Autowired

	private UserService userService;

	@GetMapping
	public List<Car> getAll() {
		return carService.getAll();
	}

	private User getUserFromToken(String token) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			String userJson = JWT.decode(token).getClaim("user").asString();
			return (User) mapper.readValue(userJson, User.class);

		} catch (Exception e) {

			return null;

		}

	}

	@PostMapping
	public boolean addCar(@RequestHeader(name = "Authorization") String token, @RequestBody Car newCar) {
		User u = User.getUserFromToken(token);
		newCar.setUser(u);
		carService.addCar(newCar);
		return true;
	}

	@PostMapping("/updateCar")
	public boolean updateCar(@RequestHeader(name = "Authorization") String token, @RequestBody Car newCar) {
		User u = User.getUserFromToken(token);
		newCar.setUser(u);
		carService.removeCar(carService.getCarForUser(u));
		carService.addCar(newCar);
		return true;
	}
	
	@PostMapping("/removeCar")
	public void removeCar(@RequestBody Car car) {
		carService.removeCar(car);
	}

	@GetMapping("/myCar")
	public Car getCar(@RequestHeader(name = "Authorization") String token) {
		User u = User.getUserFromToken(token);
		return carService.getCarForUser(u);
	}
}