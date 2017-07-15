package com.revature.rideshare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.revature.rideshare.dao.PointOfInterestRepository;
import com.revature.rideshare.domain.PointOfInterest;

@Component("poiService")
@Transactional // need??
public class PointOfInterestService {

	@Autowired
	private PointOfInterestRepository poiRepo;

	public PointOfInterestService() {
	}

	public void setPoiRepo(PointOfInterestRepository poiRepo) {
		this.poiRepo = poiRepo;
	}

	public List<PointOfInterest> getAll() {
		return poiRepo.findAll();
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
	
	public PointOfInterest getOnePoiByName(String name) {
		List<PointOfInterest> pois = poiRepo.findByPoiName(name);
		if (pois.isEmpty()) {
			return null;
		} else {
			return pois.get(0);
		}
	}
	
	public PointOfInterest getPoiByStreetAddress(String addressLine1) {
		List<PointOfInterest> pois = poiRepo.findByAddressLine1(addressLine1);
		if (pois.isEmpty()) {
			return null;
		} else {
			return pois.get(0);
		}
	}
}
