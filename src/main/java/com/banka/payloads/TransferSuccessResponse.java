package com.banka.payloads;

public class TransferSuccessResponse {

	private String sender;
	private String beneficiary;
	private String amount;
	private String transactionId;
	private String timeOfTransaction;
	private String description;
	
	public TransferSuccessResponse() {
		
	}
	
	

	public TransferSuccessResponse(String sender, String beneficiary, String amount, String transactionId,
			String timeOfTransaction, String description) {
		this.sender = sender;
		this.beneficiary = beneficiary;
		this.amount = amount;
		this.transactionId = transactionId;
		this.timeOfTransaction = timeOfTransaction;
		this.description = description;
	}



	public String getSender() {
		return sender;
	}

	public String getBeneficiary() {
		return beneficiary;
	}

	public void setBeneficiary(String beneficiary) {
		this.beneficiary = beneficiary;
	}

	public String getAmount() {
		return amount;
	}

	public String getTransactionId() {
		return transactionId;
	}


	public String getTimeOfTransaction() {
		return timeOfTransaction;
	}


	public String getDescription() {
		return description;
	}

		
}
