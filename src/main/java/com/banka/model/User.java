package com.banka.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;


@Entity
public class User {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String email;
	@Column(updatable = false, unique = true)
	private String username;
	private String password;
	private String accountNumber;
	BigDecimal accountBalance = new BigDecimal("0.00");
	
	
	@Column(updatable = false)
	private LocalDateTime created_At;
	
	private LocalDateTime updated_At;
	 
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "users_roles", 
			 joinColumns=@JoinColumn(name = "user_id"), 
		     inverseJoinColumns=@JoinColumn(name = "role_id")) 
	private Set<Role> roles = new HashSet<>();
	
	
	
	public User() {

	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
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


	public String getPhoneNumber() {
		return phoneNumber;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getAccountNumber() {
		return accountNumber;
	}


	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	


	public BigDecimal getAccountBalance() {
		return accountBalance;
	}


	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}


	public LocalDateTime getCreated_At() {
		return created_At;
	}


	public void setCreated_At(LocalDateTime created_At) {
		this.created_At = created_At;
	}


	public Set<Role> getRoles() {
		return roles;
	}


	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	@PrePersist
	protected void onCreate() {
		this.created_At = LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.updated_At = LocalDateTime.now();
	}

}
