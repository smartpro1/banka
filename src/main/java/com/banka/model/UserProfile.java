package com.banka.model;

import java.math.BigDecimal;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class UserProfile {
	
	@Override
	public String toString() {
		return "UserProfile [id=" + id + ", phoneNumber=" + phoneNumber + ", accountNumber=" + accountNumber
				+ ", accountBalance=" + accountBalance + ", transferPin=" + transferPin + ", user=" + user + "]";
	}

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	private String phoneNumber;
	private String accountNumber;
	private BigDecimal accountBalance = new BigDecimal("25000.00");
	private String transferPin;
	
	// oneToOne with User
//	@OneToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name="user_id", nullable = true)
//	@JsonIgnore
//	private User user;
	
	@OneToOne()
	@JoinColumn(name="user_id")
	private User user;
	
	

	public UserProfile() {
		
	}

	public UserProfile(String phoneNumber, String accountNumber, String transferPin) {
		this.phoneNumber = phoneNumber;
		this.accountNumber = accountNumber;
		this.transferPin = transferPin;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTransferPin() {
		return transferPin;
	}

	public void setTransferPin(String transferPin) {
		this.transferPin = transferPin;
	}
	
	
		

}
