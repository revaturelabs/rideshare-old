package com.revature.rideshare.service;

import java.util.List;

import com.revature.rideshare.domain.Car;

public interface CarService {

	List<Car> getAll();

	void addCar(Car car);

	void removeCar(Car car);

	void updateCar(Car car);

}
