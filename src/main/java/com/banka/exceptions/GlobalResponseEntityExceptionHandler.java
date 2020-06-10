package com.banka.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GlobalResponseEntityExceptionHandler extends ResponseEntityExceptionHandler{

	@ExceptionHandler
	public final ResponseEntity<Object> handleCredentialAlreadyInUseException(CredentialAlreadyInUseException ex, WebRequest req){
		CredentialAlreadyInUseExceptionResponse exceptionResponse = new CredentialAlreadyInUseExceptionResponse(ex.getMessage());
	    return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler
	public final ResponseEntity<Object> handleCredentialNotFoundException(CredentialNotFoundException ex, WebRequest req){
		CredentialNotFoundExceptionResponse exceptionResponse = new CredentialNotFoundExceptionResponse(ex.getMessage());
	    return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
	}
}
