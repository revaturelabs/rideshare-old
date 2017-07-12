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
import com.revature.rideshare.domain.AvailableRide;
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
	    public List<Car> getAll(){
	        return carService.getAll();
	 }
	 
	 
	 
	private User getUserFromToken(String token) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println("before userJson");
			String userJson = JWT.decode(token).getClaim("user").asString();
			System.out.println("in get user token " + userJson);
			return (User) mapper.readValue(userJson, User.class);
		} catch (Exception e) {
			return null;
		}
	}
	 
	 
	 
	 
	
	@PostMapping
	public boolean addCar(@RequestHeader(name="Authorization") String token, @RequestBody Car c){
		System.out.println("before");
		System.out.println(c.toString());
		User u = getUserFromToken(token);
		System.out.println("Car user " + u.toString());
		c.setUser(u);
		
		carService.addCar(c);
		return true;
	}	
	

    @PostMapping("/removeCar")
    public void removeCar(@RequestBody Car car){
        carService.removeCar(car);
    }

    @PostMapping("/updateCar")
    public void updateCar(@RequestBody Car car){
        carService.updateCar(car);
    }
}