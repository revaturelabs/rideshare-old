package com.revature.rideshare.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.rideshare.domain.AvailableRide;
import com.revature.rideshare.domain.User;

public interface AvailableRideRepository extends JpaRepository<AvailableRide, Long> {
	List<AvailableRide> findByCarUser(User u);

	List<AvailableRide> findByIsOpenFalse();
	
	List<AvailableRide> findByIsOpenTrue();
}