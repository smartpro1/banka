package com.banka.payloads;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

public class ChangePasswordRequest {

	private String token;
	@NotBlank(message="password cannot be blank")
   private String password;
	
	@Transient
	private String confirmPassword;
	
	

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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
