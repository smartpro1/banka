package com.banka.payloads;

import javax.validation.constraints.NotBlank;

public class TransactionDateRangeDto {
	@NotBlank(message= "start field is required")
	private String start;
	@NotBlank(message= "end field is required")
	private String end;
	
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	
	
}
