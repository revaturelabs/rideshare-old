package com.revature.rideshare;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.revature.rideshare.dao.UserRepository;

public class RideshareUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepo;
	
	public void setUserRepo(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String slackId) throws UsernameNotFoundException {
		com.revature.rideshare.domain.User u = userRepo.findBySlackId(slackId);
		if (u == null) {
			throw new UsernameNotFoundException("slack user id not found");
		}
		return buildUserFromUserEntity(u);
	}
	
	private User buildUserFromUserEntity(com.revature.rideshare.domain.User userEntity) {
		
	}
	
	

}
