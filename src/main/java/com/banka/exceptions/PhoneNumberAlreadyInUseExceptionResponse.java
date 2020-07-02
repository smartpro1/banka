package com.banka.exceptions;

public class PhoneNumberAlreadyInUseExceptionResponse {
   private String phoneNumber;

public PhoneNumberAlreadyInUseExceptionResponse(String phoneNumber) {
	this.phoneNumber = phoneNumber;
}

public String getPhoneNumber() {
	return phoneNumber;
}

public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
}



  
   
}
