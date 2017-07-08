package com.revature.rideshare.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import com.revature.rideshare.domain.*;


public interface UserRepository extends JpaRepository<User, Long>{

	
}
