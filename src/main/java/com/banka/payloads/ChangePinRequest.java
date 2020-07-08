package com.banka.payloads;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ChangePinRequest {
	
	@NotBlank(message="pin cannot be blank")
	@Size(min = 4, max = 4, message="pin must be exactly four characters long")
	private String currentPin;
	
	@NotBlank(message="pin cannot be blank")
	@Size(min = 4, max = 4, message="pin must be exactly four characters long")
   private String newPin;
	
	@Transient
	private String confirmNewPin;

	public String getCurrentPin() {
		return currentPin;
	}

	public void setCurrentPin(String currentPin) {
		this.currentPin = currentPin;
	}

	public String getNewPin() {
		return newPin;
	}

	public void setNewPin(String newPin) {
		this.newPin = newPin;
	}

	public String getConfirmNewPin() {
		return confirmNewPin;
	}

	public void setConfirmNewPin(String confirmNewPin) {
		this.confirmNewPin = confirmNewPin;
	}
	
	
    
	
}
