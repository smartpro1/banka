package com.banka.exceptions;

public class EmailAlreadyInUseExceptionResponse {
   private String email;

public EmailAlreadyInUseExceptionResponse(String email) {
	this.email = email;
}

public String getEmail() {
	return email;
}

public void setEmail(String email) {
	this.email = email;
}
 
   
}
