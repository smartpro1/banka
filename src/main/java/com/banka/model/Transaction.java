package com.banka.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Transaction {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	
	private String transactionType;
	private BigDecimal amount;
	private String accountNumberInvolved;
	private String description;
	private String staffInvolved;
	@Column(updatable = false, unique = true)
	private String transactionId;
	@Column(updatable = false)
	private LocalDateTime created_At;
//	private RoleName role;
	
	//@ManyToOne with User
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;
		

	public Transaction() {
	
	}
	
	
	public Transaction(String transactionType, BigDecimal amount, String accountNumberInvolved, String description,
			String staffInvolved, User user, String transactionId) {
		this.transactionType = transactionType;
		this.amount = amount;
		this.accountNumberInvolved = accountNumberInvolved;
		this.description = description;
		this.staffInvolved = staffInvolved;
		this.user = user;
		this.transactionId = transactionId;
	}






	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getTransactionType() {
		return transactionType;
	}


	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}


	public BigDecimal getAmount() {
		return amount;
	}


	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}


	public String getAccountNumberInvolved() {
		return accountNumberInvolved;
	}


	public void setAccountNumberInvolved(String accountNumberInvolved) {
		this.accountNumberInvolved = accountNumberInvolved;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getStaffInvolved() {
		return staffInvolved;
	}


	public void setStaffInvolved(String staffInvolved) {
		this.staffInvolved = staffInvolved;
	}


	public LocalDateTime getCreated_At() {
		return created_At;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}
	
	
	public String getTransactionId() {
		return transactionId;
	}


//	public void setTransactionId(String transactionId) {
//		transactionId = transactionId;
//	}


	@PrePersist
	protected void onCreate() {
		this.created_At = LocalDateTime.now();
	}
	
}
