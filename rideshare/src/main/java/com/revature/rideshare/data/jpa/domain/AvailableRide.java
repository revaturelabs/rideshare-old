package com.revature.rideshare.data.jpa.domain;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="AVAILABLE_RIDES")
public class AvailableRide implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5753230302496991697L;

	@Id
	@Column(name="AVAILABLE_RIDE_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AR_ID_SEQUENCE")
	@SequenceGenerator(name="AR_ID_SEQUENCE", sequenceName="AR_ID_SEQUENCE")
	private long availRideId;
	
	@OneToMany(mappedBy = "car", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE )
	@Column(name="CAR_ID")
	private Car car;
	
	@OneToMany(mappedBy="pointofinterest", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE )
	@Column(name="PICKUP_ID")
	private PointOfInterest pickupPOI;
	
	@OneToMany(mappedBy="pointofinterest", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE )
	@Column(name="DROPOFF_ID")
	private PointOfInterest dropoffPOI;
	
	@Column(name="AVAILABLE_SEATS")
	private short seatsAvailable;
	
	@Temporal(TemporalType.DATE)
	@Column(name="TIME")
	private Date time;
	
	@Column(name="NOTES")
	private String notes;

	public AvailableRide(){}
	
	public AvailableRide(long availRideId, Car car, PointOfInterest pickupPOI, PointOfInterest dropoffPOI,
			short seatsAvailable, Date time, String notes) {
		super();
		this.availRideId = availRideId;
		this.car = car;
		this.pickupPOI = pickupPOI;
		this.dropoffPOI = dropoffPOI;
		this.seatsAvailable = seatsAvailable;
		this.time = time;
		this.notes = notes;
	}

	public long getAvailRideId() {
		return availRideId;
	}

	public void setAvailRideId(long availRideId) {
		this.availRideId = availRideId;
	}

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}

	public PointOfInterest getPickupPOI() {
		return pickupPOI;
	}

	public void setPickupPOI(PointOfInterest pickupPOI) {
		this.pickupPOI = pickupPOI;
	}

	public PointOfInterest getDropoffPOI() {
		return dropoffPOI;
	}

	public void setDropoffPOI(PointOfInterest dropoffPOI) {
		this.dropoffPOI = dropoffPOI;
	}

	public short getSeatsAvailable() {
		return seatsAvailable;
	}

	public void setSeatsAvailable(short seatsAvailable) {
		this.seatsAvailable = seatsAvailable;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	

	
}
