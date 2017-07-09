package com.revature.rideshare.data.jpa;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.revature.rideshare.Application;
import com.revature.rideshare.domain.Car;
import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.CarService;
import com.revature.rideshare.service.UserService;

/**
 * Integration test to run the application.
 * 
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("scratch")
// Separate profile for web tests to avoid clashing databases
public class ApplicationTests {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setUp() {
		this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}

	@Test
	public void testHome() throws Exception {

		this.mvc.perform(get("/")).andExpect(status().isOk())
				.andExpect(content().string("Bath"));

    UserService us = (UserService)this.context.getBean("UserServiceImpl");
    User user = new User(1, "Chicken", "Little", null, "user@email.com", false); 
    us.addUser(user); 
    
    user.setAdmin(true);
    
    us.updateUser(user); 
    us.removeUser(user); 

    CarService cs = (CarService)this.context.getBean("CarServiceImpl");
    Car car = new Car(); 
    cs.addCar(car);

    car.setBrand("Honda");

    cs.updateCar(car); 
    cs.removeCar(car);
	}
}
