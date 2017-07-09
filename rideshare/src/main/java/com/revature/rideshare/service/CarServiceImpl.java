package com.revature.rideshare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.revature.rideshare.dao.CarRepository;
import com.revature.rideshare.domain.Car;

@Component("carService")
@Transactional
public class CarServiceImpl implements CarService{
	
	private final CarRepository carRepo;

	@Autowired
	public CarServiceImpl(CarRepository carRepo){
		this.carRepo = carRepo;
	}
	
	@Override
	public void addUser(Car c) {
		carRepo.saveAndFlush(c);
		
	}

}
