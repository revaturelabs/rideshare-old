package com.revature.rideshare.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.rideshare.domain.Ride;

public interface RideRepository extends JpaRepository<Ride, Long> {

}
