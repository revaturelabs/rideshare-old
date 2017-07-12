package com.revature.rideshare.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.revature.rideshare.domain.AvailableRide;
import com.revature.rideshare.domain.User;

public interface AvailableRideRepository extends JpaRepository<AvailableRide, Long>{
	List<AvailableRide> findByCarUser(User u);

//	@Query("HQL GOES HERE")
//    public List<AvailableRide> findOpen();
}
