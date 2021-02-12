package com.banka.payloads;


public class TransferRequestPayload {
	
	private String benfAcctNum;
	private String amount;
	private String description;
	private String pin;
	
	
	public String getBenfAcctNum() {
		return benfAcctNum;
	}
	public void setBenfAcctNum(String benfAcctNum) {
		this.benfAcctNum = benfAcctNum;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	
	
	
	
	
}
