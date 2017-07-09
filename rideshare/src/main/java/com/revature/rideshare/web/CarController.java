package com.revature.rideshare.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.domain.Car;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.CarService;

@RestController
@RequestMapping("car")
public class CarController {
	
	@Autowired
	private CarService carService;
	
	@PostMapping
	public boolean addCar(@RequestBody Car c, Authentication authentication){
		System.out.println("before");
		User u = (User) ((PreAuthenticatedAuthenticationToken) authentication).getPrincipal();
		System.out.println("test");
		System.out.println(u.toString());
		System.out.println(c.toString());
		c.setUser(u);
		carService.addCar(c);
		return true;
	}	
}
