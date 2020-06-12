package com.banka.exceptions;

public class CredentialAlreadyInUseExceptionResponse {
   private String credentialInUseException;

public CredentialAlreadyInUseExceptionResponse(String credentialInUseException) {
	this.credentialInUseException = credentialInUseException;
}

public String getCredentialInUseException() {
	return credentialInUseException;
}

public void setCredentialInUseException(String credentialInUseException) {
	this.credentialInUseException = credentialInUseException;
}

  
   
}
