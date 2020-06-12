package com.banka.services;

import org.springframework.stereotype.Service;

import com.banka.model.User;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

@Service
public class SMSServiceImpl implements SMSService {

	public static final String ACCOUNT_SID = "AC49fb855ca34110be1390b8b7948e36d1";
	public static final String AUTH_TOKEN = "9f502e21a2814083e60949e3ba752c17";
	
	
	@Override
	public Message sendSMS(User user) {
		String acctNum = user.getAccountNumber();
		String firstName = user.getFirstName();
		String lastName = user.getLastName();
		String phoneNumber = "+234" + user.getPhoneNumber();
		String output = String.format("Dear %s %s, your account registration was successful and your account number is %s, login to update your profile", 
				                 firstName, lastName, acctNum);
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		Message message = Message.creator(
				new com.twilio.type.PhoneNumber(phoneNumber), 
				new com.twilio.type.PhoneNumber("+12057844773"), 
				output)
				.create();
		return message;
	}

}
