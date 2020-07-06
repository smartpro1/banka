package com.banka.payloads;

import javax.validation.constraints.NotBlank;

public class PasswordResetRequest {
	@NotBlank(message = "email is required")
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
