package com.revature.rideshare.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="POINTS_OF_INTEREST")
public class PointOfInterest implements Serializable {

	private static final long serialVersionUID = 1610039859397834102L;

	@Id
	@Column(name="POI_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POI_ID_SEQUENCE")
	@SequenceGenerator(name="POI_ID_SEQUENCE", sequenceName="POI_ID_SEQUENCE")
	private int poiId;
	
	@Column(name="NAME", nullable=false)
	private String poiName;
	
	@Column(name="ADDRESS_1", nullable=false)
	private String addressLine1;
	
	@Column(name="ADDRESS_2")
	private String addressLine2;
	
	@Column(name="CITY", nullable=false)
	private String city;
	
	@Column(name="STATE", nullable=false)
	private String state;
	
	@Column(name="ZIP", nullable=false)
	private short zipCode;
	
	@Column(name="LATITUDE", nullable=false, scale=6)
	private BigDecimal latitude;
	
	@Column(name="LONGITUDE", nullable=false, scale=6)
	private BigDecimal longitude;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE )
	private PointOfInterestType type;
	
	public PointOfInterest(){}

	public PointOfInterest(int poiId, String poiName, String addressLine1, String addressLine2, String city,
			String state, short zipCode, BigDecimal latitude, BigDecimal longitude, PointOfInterestType type) {
		super();
		this.poiId = poiId;
		this.poiName = poiName;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.state = state;
		this.zipCode = zipCode;
		this.latitude = latitude;
		this.longitude = longitude;
		this.type = type;
	}

	public int getPoiId() {
		return poiId;
	}

	public void setPoiId(int poiId) {
		this.poiId = poiId;
	}

	public String getPoiName() {
		return poiName;
	}

	public void setPoiName(String poiName) {
		this.poiName = poiName;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public short getZipCode() {
		return zipCode;
	}

	public void setZipCode(short zipCode) {
		this.zipCode = zipCode;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public PointOfInterestType getType() {
		return type;
	}

	public void setType(PointOfInterestType type) {
		this.type = type;
	}
	
	
	
}
