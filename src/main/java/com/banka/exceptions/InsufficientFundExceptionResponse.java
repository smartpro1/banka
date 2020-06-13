package com.banka.exceptions;

public class InsufficientFundExceptionResponse {
   private String exception;
   
   
   public InsufficientFundExceptionResponse(String exception) {
	 this.exception = exception;
    }

	public String getException() {
		return exception;
	}
	
	public void setException(String exception) {
		this.exception = exception;
	}
	 
   
   
}
