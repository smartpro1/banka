package com.banka.payloads;


public class WithdrawalRequestPayload {
	
	private String accountNumber;
	private String amountToWithdraw;
	private String staffPin;
	
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getAmountToWithdraw() {
		return amountToWithdraw;
	}
	public void setAmountToWithdraw(String amountToWithdraw) {
		this.amountToWithdraw = amountToWithdraw;
	}
	public String getStaffPin() {
		return staffPin;
	}
	public void setStaffPin(String staffPin) {
		this.staffPin = staffPin;
	}
	
	
	
	
}
