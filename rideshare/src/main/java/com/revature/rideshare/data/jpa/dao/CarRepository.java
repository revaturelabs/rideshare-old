package com.revature.rideshare.data.jpa.dao;

import org.springframework.data.repository.Repository;

import com.revature.rideshare.data.jpa.domain.*;

public interface CarRepository extends Repository<Car, Long>{

	Car findByUser(User user);
	
}
