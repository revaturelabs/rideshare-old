package com.revature.rideshare.data.jpa.service;

import java.util.List;

import com.revature.rideshare.data.jpa.domain.User;

public interface UserService {


	List<User> getAll();

	void addUser(User u);

	
}