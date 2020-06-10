package com.banka.payloads;

import javax.persistence.Column;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserRegPayload {
   
	@NotBlank(message="please enter your firstname")
	private String firstName;
	@NotBlank(message="please enter your lastname")
	private String lastName;
	@NotBlank(message="please enter your phone number")
	@Size(min = 11, max = 11, message="phone number must be exactly eleven digits")
	private String phoneNumber;
	@NotBlank(message="please enter your email")
	private String email;
	@Size(min =5, message="username cannot be empty or less than five characters")
	@Column(updatable = false, unique = true)
	private String username;
	@Size(min =6, message="password cannot be empty or less than six characters")
	private String password;
	
	@Transient
	private String confirmPassword;
	
	public UserRegPayload (){
		
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	};
	
	
}
