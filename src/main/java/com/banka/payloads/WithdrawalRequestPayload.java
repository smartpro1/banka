package com.banka.payloads;


public class WithdrawalRequestPayload {
	
	private String accountNumber;
	private String amountToWithdraw;
	
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
	
	
}
