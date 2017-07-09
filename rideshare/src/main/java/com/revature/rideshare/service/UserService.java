package com.revature.rideshare.service;

import java.util.List;

import com.revature.rideshare.domain.User;

public interface UserService {


	List<User> getAll();

	void addUser(User u);

<<<<<<< HEAD
	User getUserBySlackId(String slackId);

=======
	User getUser(long id);
>>>>>>> 3dcc8e79fb386547b486f2bad1c5ea97ac448db7
	
}
