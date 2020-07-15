package com.banka.payloads;

import java.math.BigDecimal;

public class AccountInfoResponse {
	
	 private String accountNumber;
	 private BigDecimal accountBalance;
	 
	 public AccountInfoResponse(String accountNumber, BigDecimal accountBalance) {
		this.accountNumber = accountNumber;
		this.accountBalance = accountBalance;
	 }
	
	 public String getAccountNumber() {
		return accountNumber;
	 }
	
	 public BigDecimal getAccountBalance() {
		return accountBalance;
	 }

	@Override
	public String toString() {
		return "AccountInfoResponse [accountNumber=" + accountNumber + ", accountBalance=" + accountBalance + "]";
	}
	 
	 
}
