package com.revature.rideshare.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.rideshare.domain.Ride;
import com.revature.rideshare.domain.User;

public interface RideRepository extends JpaRepository<Ride, Long> {
	List<Ride> findByAvailRideCarUser(User u);

	List<Ride> findByRequestUser(User u);

	List<Ride> findByAvailRideCarUserOrRequestUser(User u, User u2);
}
