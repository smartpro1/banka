package com.banka.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailSendingException extends RuntimeException{

	public EmailSendingException(String message) {
		super(message);
	}

	
}
