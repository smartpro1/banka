package com.banka.exceptions;

public class CredentialNotFoundExceptionResponse {
   private String credential;

public CredentialNotFoundExceptionResponse(String credential) {
	this.credential = credential;
}

public String getCredential() {
	return credential;
}

public void setCredential(String credential) {
	this.credential = credential;
}
   
   
}
