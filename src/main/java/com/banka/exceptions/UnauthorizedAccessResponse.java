package com.banka.exceptions;

public class UnauthorizedAccessResponse {
	private String username;
	private String password;
	
	public UnauthorizedAccessResponse() {
		this.username = "invalid username";
		this.password = "invalid password";
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
}
