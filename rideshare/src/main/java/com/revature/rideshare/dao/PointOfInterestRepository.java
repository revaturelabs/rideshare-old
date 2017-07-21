package com.revature.rideshare.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.rideshare.domain.PointOfInterest;

public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, Long> {

	PointOfInterest findBypoiId(int id);
	
	PointOfInterest findBypoiName(String name);
}
