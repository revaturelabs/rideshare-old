package com.revature.rideshare.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="Rides")
public class Ride implements Serializable{

	private static final long serialVersionUID = -2957865032918745458L;

	@Id
	@Column(name="RIDE_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "R_ID_SEQUENCE")
	@SequenceGenerator(name="R_ID_SEQUENCE", sequenceName="R_ID_SEQUENCE")
	private long rideId;
	
	@OneToOne(fetch=FetchType.LAZY)
	private AvailableRide availRide;
	
	@OneToOne(fetch=FetchType.LAZY)
	private RideRequest request;
	
	@Column(name="DRIVER_RATING")
	private short driverRating;
	
	@Column(name="RIDER_RATING")
	private short riderRating;

	public Ride(){}
	
	public Ride(long rideId, AvailableRide availRide, RideRequest request, short driverRating, short riderRating) {
		super();
		this.rideId = rideId;
		this.availRide = availRide;
		this.request = request;
		this.driverRating = driverRating;
		this.riderRating = riderRating;
	}

	public long getRideId() {
		return rideId;
	}

	public void setRideId(long rideId) {
		this.rideId = rideId;
	}

	public AvailableRide getAvailRide() {
		return availRide;
	}

	public void setAvailRide(AvailableRide availRide) {
		this.availRide = availRide;
	}

	public RideRequest getRequest() {
		return request;
	}

	public void setRequest(RideRequest request) {
		this.request = request;
	}

	public short getDriverRating() {
		return driverRating;
	}

	public void setDriverRating(short driverRating) {
		this.driverRating = driverRating;
	}

	public short getRiderRating() {
		return riderRating;
	}

	public void setRiderRating(short riderRating) {
		this.riderRating = riderRating;
	}
	
	
}
