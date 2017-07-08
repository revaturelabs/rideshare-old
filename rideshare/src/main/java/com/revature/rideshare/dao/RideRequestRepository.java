package com.revature.rideshare.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.rideshare.domain.RideRequest;

public interface RideRequestRepository extends JpaRepository<RideRequest, Long>{

}
