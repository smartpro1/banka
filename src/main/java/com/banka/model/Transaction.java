package com.banka.model;

import java.math.BigDecimal;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Transaction {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	protected Long id;
	
	protected TransactionType transactionType;
	private BigDecimal amount;
	private String accountNumberInvolved;
	private String username;
	private RoleName role;
	
	//@ManyToOne with User
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;
		

	public Transaction() {
	
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public RoleName getRole() {
		return role;
	}

	public void setRole(RoleName role) {
		this.role = role;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

   	
	
}
