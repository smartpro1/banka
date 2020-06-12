package com.banka.exceptions;

public class InvalidCredentialExceptionResponse {
   private String invalidCredentialException;

public InvalidCredentialExceptionResponse(String invalidCredentialException) {
	this.invalidCredentialException = invalidCredentialException;
}

public String getInvalidCredentialException() {
	return invalidCredentialException;
}

public void setInvalidCredentialException(String invalidCredentialException) {
	this.invalidCredentialException = invalidCredentialException;
}



 
   
}
