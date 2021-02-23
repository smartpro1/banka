package com.banka.payloads;

import java.math.BigDecimal;
public class TransactionDto {
	
	private String transactionType;
	private BigDecimal amount;
	private String accountNumberInvolved;
	private String description;
	private String staffInvolved;
	private String transactionId;
	private String created_At;
	
	public TransactionDto(String transactionType, BigDecimal amount, String accountNumberInvolved, String description,
			String staffInvolved, String transactionId, String created_At) {
		
		this.transactionType = transactionType;
		this.amount = amount;
		this.accountNumberInvolved = accountNumberInvolved;
		this.description = description;
		this.staffInvolved = staffInvolved;
		this.transactionId = transactionId;
		this.created_At = created_At;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getAccountNumberInvolved() {
		return accountNumberInvolved;
	}

	public String getDescription() {
		return description;
	}

	public String getStaffInvolved() {
		return staffInvolved;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public String getCreated_At() {
		return created_At;
	}

	@Override
	public String toString() {
		return "TransactionDto [transactionType=" + transactionType + ", amount=" + amount + ", accountNumberInvolved="
				+ accountNumberInvolved + ", description=" + description + ", staffInvolved=" + staffInvolved
				+ ", transactionId=" + transactionId + ", created_At=" + created_At + "]";
	}
	
	

}
