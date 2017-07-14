package com.revature.rideshare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.revature.rideshare.dao.UserRepository;
import com.revature.rideshare.domain.User;

@Component("userService")
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;

	public UserServiceImpl() {
	}

	public void setUserRepo(UserRepository userRepo) {
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

	@Override
	public User getUser(long id) {
		return userRepo.getOne(id);
	}

	@Override
	public User getUserBySlackId(String slackId) {
		return userRepo.findBySlackId(slackId);
	}

	@Override
	public void removeUser(User user) {
		userRepo.delete(user);
	}

	@Override
	public void updateUser(User user) {
		userRepo.saveAndFlush(user);
	}
}
