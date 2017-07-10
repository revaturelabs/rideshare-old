package com.revature.rideshare.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.domain.Car;
import com.revature.rideshare.domain.Ride;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.CarService;
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
	 
	 @GetMapping("/admin/cars")
	     public List<Car> getAllCars(){
		 	List<Car> cars = new ArrayList<Car>();
		 	cars = carService.getAll();
		 	System.out.println(cars);
	        return carService.getAll();
	 }
	 
	 @GetMapping("/admin/users")
	    public List<User> getAllUsers(){
	        return userService.getAll();
	 }
	 
	 @GetMapping("/admin/rides")
	 public List<Ride> getAllRides() {
		 return rideService.getAll();
	 }
}
