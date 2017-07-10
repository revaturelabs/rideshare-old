package com.revature.rideshare.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.domain.Car;
import com.revature.rideshare.service.CarService;
import java.util.List;

@RestController
@RequestMapping("carController")
public class CarController{

    @Autowired
    private CarService carService;

    @GetMapping
    public List<Car> getAll(){
        return carService.getAll();
    }

    @PostMapping("/addCar")
    public void addCar(@RequestBody Car car){
        System.out.println(car.toString());
        carService.addCar(car);
    }

    @PostMapping("/removeCar")
    public void removeCar(@RequestBody Car car){
        carService.removeCar(car);
    }

    @PostMapping("/updateCar")
    public void updateCar(@RequestBody Car car){
        carService.updateCar(car);
    }
}