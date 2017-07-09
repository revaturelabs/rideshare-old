package com.revature.rideshare.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.rideshare.domain.Ride;
import com.revature.rideshare.domain.User;

public interface RideRepository extends JpaRepository<Ride, Long> {
	List<Ride> findByRequestUserUserId(long userId);

	List<Ride> findByRequestUser(User u);

}
