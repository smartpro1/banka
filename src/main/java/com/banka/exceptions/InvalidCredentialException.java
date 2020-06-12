package com.banka.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCredentialException extends RuntimeException{

	public InvalidCredentialException(String message) {
		super(message);
	}
  
}
