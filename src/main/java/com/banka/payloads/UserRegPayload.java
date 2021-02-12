package com.banka.payloads;

import javax.persistence.Column;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserRegPayload {
   
	@NotBlank(message="please enter your fullname")
	@Size(min = 6, max = 40, message="fullname name must be between 6 - 40 characters long")
	private String fullname;
	@NotBlank(message="please enter sex")
	private String sex;
	private String phoneNumber;
	@NotBlank(message="please enter your email")
	@Email(message="your email is invalid")
	private String email;
	@Size(min = 4, message="username cannot be empty or less than four characters")
	@Column(updatable = false, unique = true) // properly test for this
	private String username;
	private String role;
	@Size(min =6, message="password cannot be empty or less than six characters")
	private String password;
	
	@Transient
	private String confirmPassword;
	
	public UserRegPayload (){
		
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
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
	}

    
	
}
