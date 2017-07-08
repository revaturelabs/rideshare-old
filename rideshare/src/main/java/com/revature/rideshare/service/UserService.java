package com.revature.rideshare.service;

import java.util.List;

import com.revature.rideshare.domain.User;

public interface UserService {


	List<User> getAll();

	void addUser(User u);

	
}
