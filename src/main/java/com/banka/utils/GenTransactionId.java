package com.banka.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class GenTransactionId {

	public static String  generateTransactionId() {
		String transactionId = UUID.randomUUID().toString();
	    transactionId = transactionId.replaceAll("-", "");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHssSSSS");
		transactionId = transactionId.substring(20) + sdf.format(date) + transactionId.substring(3,8);
	    return transactionId;	
	}
}
