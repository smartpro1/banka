package com.banka.exceptions;

public class InvalidPhoneNumberExceptionResponse {
   private String phoneNumber;

public InvalidPhoneNumberExceptionResponse(String phoneNumber) {
	this.phoneNumber = phoneNumber;
}

public String getPhoneNumber() {
	return phoneNumber;
}

public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
}
   
}
