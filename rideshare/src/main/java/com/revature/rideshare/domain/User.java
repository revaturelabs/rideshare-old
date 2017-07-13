package com.revature.rideshare.domain;

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

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "USERS")
public class User implements Serializable {

	private static final long serialVersionUID = -2923889374579038772L;

	@Id
	@Column(name = "USER_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_ID_SEQUENCE")
	@SequenceGenerator(name = "USER_ID_SEQUENCE", sequenceName = "USER_ID_SEQUENCE")
	private long userId;

	@Column(name = "FIRST_NAME")
	private String firstName;

	@Column(name = "LAST_NAME")
	private String lastName;

	@Column(name = "FULL_NAME", nullable = false)
	private String fullName;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private PointOfInterest mainPOI;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private PointOfInterest workPOI;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "SLACK_ID", nullable = false)
	private String slackId;

	@Column(name = "IS_ADMIN", nullable = false)
	private boolean isAdmin;

	public User() {
	}

	public User(long userId, String firstName, String lastName, String fullName, PointOfInterest mainPOI,
			PointOfInterest workPOI, String email, String slackId, boolean isAdmin) {
		super();
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.fullName = fullName;
		this.mainPOI = mainPOI;
		this.workPOI = workPOI;
		this.email = email;
		this.slackId = slackId;
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public PointOfInterest getMainPOI() {
		return mainPOI;
	}

	public void setMainPOI(PointOfInterest mainPOI) {
		this.mainPOI = mainPOI;
	}

	public PointOfInterest getWorkPOI() {
		return workPOI;
	}

	public void setWorkPOI(PointOfInterest workPOI) {
		this.workPOI = workPOI;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSlackId() {
		return slackId;
	}

	public void setSlackId(String slackId) {
		this.slackId = slackId;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", fullName="
				+ fullName + ", mainPOI=" + mainPOI + ", workPOI=" + workPOI + ", email=" + email + ", slackId="
				+ slackId + ", isAdmin=" + isAdmin + "]";
	}

	public static User getUserFromToken(String token) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			String userJson = JWT.decode(token).getClaim("user").asString();
			return (User) mapper.readValue(userJson, User.class);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());

			return null;
		}
	}

}
