package com.banka.services;

import java.math.BigDecimal;

import com.banka.model.User;
import com.banka.model.UserProfile;
import com.twilio.rest.api.v2010.account.Message;

public interface SMSService {

	public Message sendSMS(UserProfile userProfile, String transactionType, BigDecimal amount, String theOtherAccountNumber);

	public Message sendSMS(String fullname, String phoneNumber, String accountNumber);
}
