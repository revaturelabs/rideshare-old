package com.revature.rideshare;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.revature.rideshare.dao.UserRepository;

/**
 * TODO: it would probably be better to make our User POJO a subclass of the
 * spring security user class if we actually want to use this class
 * 
 * @author Eric Christie
 * @created July 13, 2017
 */
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
		String username = userEntity.getEmail();
		String password = "we aren't storing any passwords";
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		if (userEntity.isAdmin()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
		return new User(username, password, authorities);
	}

}
