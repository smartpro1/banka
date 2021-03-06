package com.banka.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static com.banka.model.UserStatus.REGISTRATION_NOT_CONFIRMED;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
public class User {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	private String fullname;
	private String sex;
	@Column(unique = true)
	private String email;
	@Column(updatable = false, unique = true)
	private String username;
	private String password;
	private String isActive = REGISTRATION_NOT_CONFIRMED.name();
	private String transferPin;

	
	@Column(updatable = false)
	private LocalDateTime created_At;
	
	private LocalDateTime updated_At;
	 
	// manyToMany with roles 
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_roles", 
			 joinColumns=@JoinColumn(name = "user_id"), 
		     inverseJoinColumns=@JoinColumn(name = "role_id")) 
	private Set<Role> roles = new HashSet<>();
	
	// oneToMany with Transaction
	@OneToMany(cascade = CascadeType.ALL, mappedBy="user")
	private List<Transaction> transactions = new ArrayList<>();
	

	
	public User() {

	}
	
	
	public User(String fullname, String sex, String username, String email, String password, String transferPin) {
		this.fullname = fullname;
		this.sex = sex;
		this.email = email;
		this.username = username;
		this.password = password;
		this.transferPin = transferPin;
	}


    


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getFullname() {
		return fullname;
	}


	public void setFullname(String fullname) {
		this.fullname = fullname;
	}


	public String getSex() {
		return sex;
	}


	public void setSex(String sex) {
		this.sex = sex;
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


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public LocalDateTime getCreated_At() {
		return created_At;
	}


	public void setCreated_At(LocalDateTime created_At) {
		this.created_At = created_At;
	}


	public LocalDateTime getUpdated_At() {
		return updated_At;
	}


	public void setUpdated_At(LocalDateTime updated_At) {
		this.updated_At = updated_At;
	}


	public Set<Role> getRoles() {
		return roles;
	}


	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}


	public List<Transaction> getTransactions() {
		return transactions;
	}


	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}



	public String getIsActive() {
		return isActive;
	}


	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	
	


	public String getTransferPin() {
		return transferPin;
	}


	public void setTransferPin(String transferPin) {
		this.transferPin = transferPin;
	}


	@PrePersist
	protected void onCreate() {
		this.created_At = LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.updated_At = LocalDateTime.now();
	}


	@Override
	public String toString() {
		return "User [id=" + id + ", fullname=" + fullname + ", sex=" + sex + ", email=" + email + ", username="
				+ username + ", password=" + password + ", isActive=" + isActive + ", transferPin=" + transferPin
				+ ", created_At=" + created_At + ", updated_At=" + updated_At + ", roles=" + roles + ", transactions="
				+ transactions + "]";
	}


	


	

	
}
