package com.banka.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientFundException extends RuntimeException{

	public InsufficientFundException(String message) {
		super(message);
	}
  
}
