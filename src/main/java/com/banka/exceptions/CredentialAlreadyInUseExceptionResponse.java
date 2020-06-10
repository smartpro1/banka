package com.banka.exceptions;

public class CredentialAlreadyInUseExceptionResponse {
   private String credential;

public CredentialAlreadyInUseExceptionResponse(String credential) {
	this.credential = credential;
}

public String getCredential() {
	return credential;
}

public void setCredential(String credential) {
	this.credential = credential;
}
   
   
}
