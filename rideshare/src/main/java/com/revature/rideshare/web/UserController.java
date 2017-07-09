/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.revature.rideshare.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.rideshare.domain.User;
import com.revature.rideshare.service.UserService;

@RestController
@RequestMapping("sample")
public class SampleController {

	@Autowired
	private UserService userService;

	@Transactional(readOnly = true)
	@GetMapping
	public ArrayList<String> helloWorld() {
		List<User> users = 	userService.getAll();
		ArrayList<String> names = new ArrayList<String>();
		for(User u:users){
			String temp = u.getFirstName() + " " + u.getLastName();
			System.out.println("hello " + temp);
			names.add(temp);
		}
		return names;	
	}
	
	@PostMapping
	public void addUser(@RequestBody User u){
		System.out.println(u.toString());
		userService.addUser(u);
	}
	
}
