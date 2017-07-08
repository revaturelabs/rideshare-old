package com.revature.rideshare.dao;

import org.springframework.data.repository.Repository;

import com.revature.rideshare.domain.*;

public interface CarRepository extends Repository<Car, Long>{

	Car findByUser(User user);
	
}
