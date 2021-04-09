package com.banka.payloads;

import java.util.ArrayList;
import java.util.List;

import com.banka.model.Transaction;

public class TransferSuccessResponse {

	private String sender;
	private String beneficiary;
	private String amount;
	private String transactionId;
	private String timeOfTransaction;
	private String description;
	private String benfAcctNum;
	private String senderAcctBal;
	List<TransactionDto> transactions = new ArrayList<>();
	
	public TransferSuccessResponse() {
		
	}
	
	

	public TransferSuccessResponse(String sender, String beneficiary, String amount, String transactionId,
			String timeOfTransaction, String description, String benfAcctNum, String senderAcctBal, List<TransactionDto> senderTransactionz) {
		this.sender = sender;
		this.beneficiary = beneficiary;
		this.amount = amount;
		this.transactionId = transactionId;
		this.timeOfTransaction = timeOfTransaction;
		this.description = description;
		this.benfAcctNum = benfAcctNum;
		this.senderAcctBal =  senderAcctBal;
		this.transactions = senderTransactionz;
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



	public String getBenfAcctNum() {
		return benfAcctNum;
	}



	public String getSenderAcctBal() {
		return senderAcctBal;
	}



	public List<TransactionDto> getTransactions() {
		return transactions;
	}
	
	
    
		
}
