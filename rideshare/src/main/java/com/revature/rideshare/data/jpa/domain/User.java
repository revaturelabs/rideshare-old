package com.revature.rideshare.data.jpa.domain;

import java.io.Serializable;

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
@Table(name="USERS")
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2923889374579038772L;

	@Id
	@Column(name="USER_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_ID_SEQUENCE")
	@SequenceGenerator(name="USER_ID_SEQUENCE", sequenceName="USER_ID_SEQUENCE")
	private long userId;
	
	@Column(name="FIRST_NAME")
	private String firstName;
	
	@Column(name="LAST_NAME")
	private String lastName;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private PointOfInterest mainPOI;
	
	@Column(name="EMAIL")
	private String email;
	
	@Column(name="PASSWORD")
	private String password;
	
	@Column(name="IS_ADMIN")
	private boolean isAdmin;

	public User(){}
	
	public User(long userId, String firstName, String lastName, PointOfInterest mainPOI, String email, String password,
			boolean isAdmin) {
		super();
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mainPOI = mainPOI;
		this.email = email;
		this.password = password;
		this.isAdmin = isAdmin;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public PointOfInterest getMainPOI() {
		return mainPOI;
	}

	public void setMainPOI(PointOfInterest mainPOI) {
		this.mainPOI = mainPOI;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
}
