package com.revature.rideshare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.revature.rideshare.dao.PointOfInterestRepository;
import com.revature.rideshare.dao.PointOfInterestTypeRepository;
import com.revature.rideshare.domain.PointOfInterest;
import com.revature.rideshare.domain.PointOfInterestType;

@Component("poiService")
@Transactional // need??
public class PointOfInterestService {

	@Autowired
	private PointOfInterestRepository poiRepo;
	@Autowired
	private PointOfInterestTypeRepository poiTypeRepo;

	public PointOfInterestService() {
	}

	public void setPoiRepo(PointOfInterestRepository poiRepo) {
		this.poiRepo = poiRepo;
	}

	public void setPoiTypeRepo(PointOfInterestTypeRepository poiTypeRepo) {
		this.poiTypeRepo = poiTypeRepo;
	}

	public List<PointOfInterest> getAll() {
		return poiRepo.findAll();
	}

	public List<PointOfInterestType> getAllTypes() {
		return poiTypeRepo.findAll();
	}

	public void addPoi(PointOfInterest poi) {
		poiRepo.saveAndFlush(poi);
	}

	public void removePoi(PointOfInterest poi) {
		poiRepo.delete(poi);
	}

	public void updatePoi(PointOfInterest poi) {
		poiRepo.saveAndFlush(poi);
	}

	public PointOfInterest getPoi(long id) {
		return poiRepo.getOne(id);
	}
}
