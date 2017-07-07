package com.revature.rideshare.data.jpa.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="CARS")
public class Car implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8165384680317463511L;

	@Id
	@Column(name="CAR_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CAR_ID_SEQUENCE")
	@SequenceGenerator(name="CAR_ID_SEQUENCE", sequenceName="CAR_ID_SEQUENCE")
	private long carId;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="USER_ID")
	private User user;

	@Column(name="LICENSE_PLATE")
	private String licensePlate;
	
	@Column(name="CAR_BRAND")
	private String brand;
	
	@Column(name="CAR_MODEL")
	private String model;

	@Column(name="CAR_COLOR")
	private String color;

	@Column(name="IS_SMOKE_FREE")
	private boolean smokeFree;

	@Column(name="CAR_NOTES")
	private String notes;
	
	public Car(){}

	public Car(long carId, User user, String licensePlate, String brand, String model, String color, boolean smokeFree,
			String notes) {
		super();
		this.carId = carId;
		this.user = user;
		this.licensePlate = licensePlate;
		this.brand = brand;
		this.model = model;
		this.color = color;
		this.smokeFree = smokeFree;
		this.notes = notes;
	}

	public long getCarId() {
		return carId;
	}

	public void setCarId(long carId) {
		this.carId = carId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isSmokeFree() {
		return smokeFree;
	}

	public void setSmokeFree(boolean smokeFree) {
		this.smokeFree = smokeFree;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
	
	
}
