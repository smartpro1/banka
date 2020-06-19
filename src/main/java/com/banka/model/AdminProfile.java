package com.banka.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class AdminProfile {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	
	private String jobDescription;
	
	// oneToOne with UserProfile
//	@OneToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name="user_id", nullable = false)
//	@JsonIgnore
//	private User admin;
	
	@OneToOne()
	@JoinColumn(name="user_id")
	private User admin;
	
	

	public AdminProfile(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public User getAdmin() {
		return admin;
	}

	public void setAdmin(User admin) {
		this.admin = admin;
	}

	
	
	
	
}
