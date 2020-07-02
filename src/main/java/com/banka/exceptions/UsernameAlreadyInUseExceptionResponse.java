package com.banka.exceptions;

public class UsernameAlreadyInUseExceptionResponse {
   private String username;

public UsernameAlreadyInUseExceptionResponse(String username) {
	this.username = username;
}

public String getUsername() {
	return username;
}

public void setUsername(String username) {
	this.username = username;
}

 
   
}
