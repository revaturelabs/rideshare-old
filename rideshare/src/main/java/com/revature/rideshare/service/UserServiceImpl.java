package com.revature.rideshare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.revature.rideshare.dao.UserRepository;
import com.revature.rideshare.domain.User;

@Component("userService")
@Transactional
public class UserServiceImpl implements UserService{

	private final UserRepository userRepo;
	
	@Autowired
	public UserServiceImpl(UserRepository userRepo){
		this.userRepo = userRepo;
	}

	@Override
	public List<User> getAll() {
			return userRepo.findAll();
	}

	@Override
	public void addUser(User u) {
		userRepo.saveAndFlush(u);
	}
	
}
