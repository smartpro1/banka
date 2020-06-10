package com.banka.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CredentialAlreadyInUseException extends RuntimeException{

	public CredentialAlreadyInUseException(String message) {
		super(message);
	}

	
}
