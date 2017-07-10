package com.revature.rideshare.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.UserService;

@RestController
@RequestMapping("user")
public class UserController {
	@Autowired
	private UserService userService;

	@RequestMapping("/id/{id}")
	public @ResponseBody User getUser(@PathVariable(value = "id") long id) {
		return userService.getUser(id);
	}

	@RequestMapping("/me")
	public User getCurrentUser() {
		return userService.getUser(1);
	}
	
}
