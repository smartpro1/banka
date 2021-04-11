package com.banka.payloads;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class PinResetRequest {

	@NotBlank(message = "email is required")
	@Email(message = "data must be of email type")
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}

