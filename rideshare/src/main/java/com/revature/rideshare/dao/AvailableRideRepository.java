package com.revature.rideshare.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.rideshare.domain.AvailableRide;

public interface AvailableRideRepository extends JpaRepository<AvailableRide, Long>{

}
