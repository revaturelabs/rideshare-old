package com.revature.rideshare.service;

import com.revature.rideshare.domain.Car;
import java.util.List;

public interface CarService {
	
	List<Car> getAll();
	
	void addCar(Car car);
	void removeCar(Car car);
	void updateCar(Car car);

}

