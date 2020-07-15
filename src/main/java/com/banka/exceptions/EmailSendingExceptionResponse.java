package com.banka.exceptions;

public class EmailSendingExceptionResponse {
   private String email;

public EmailSendingExceptionResponse(String email) {
	this.email = email;
}

public String getEmail() {
	return email;
}

public void setEmail(String email) {
	this.email = email;
}
 
   
}
