package com.banka.payloads;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class Operation {
	@NotBlank(message="account number cannot be blank")
    @Pattern(regexp="0\\d{9}", message="account number must be exactly 10 digits")  
	String acctNum;
    @Pattern(regexp="\\d{4,8}", message="pin must be exactly 4 to 8 digits")  
	String pin;
    @NotBlank(message="status is required") 
	String status;
    
    public Operation() {}

	public String getAcctNum() {
		return acctNum;
	}

	public void setAcctNum(String acctNum) {
		this.acctNum = acctNum;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Operation [acctNum=" + acctNum + ", pin=" + pin + ", status=" + status + "]";
	}
    
    
}
