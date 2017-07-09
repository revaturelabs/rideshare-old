package com.revature.rideshare.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.domain.Car;
import com.revature.rideshare.service.CarService;

@RestController
@RequestMapping("car")
public class CarController {
	
	@Autowired
	private CarService carService;
	
	@PostMapping
	public void addCar(@RequestBody Car c){
		System.out.println(c.toString());
		carService.addUser(c);
	}	
}
