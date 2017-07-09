package com.revature.rideshare.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.revature.rideshare.domain.RideRequest;
import com.revature.rideshare.domain.User;

public interface RideRequestRepository extends JpaRepository<RideRequest, Long>{
	List<RideRequest> findByUser(User u);

//	 @Query("HQL GOES HERE")
//	 public List<RideRequest> findOpen();
}
