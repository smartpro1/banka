package com.banka.services;

import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

@Service
public class SMSServiceImpl implements SMSService {

	public static final String ACCOUNT_SID = "AC49fb855ca34110be1390b8b7948e36d1";
	public static final String AUTH_TOKEN = "5ef4abc05c0a0028d277622127a3f36c";
	
	@Override
	public Message sendSMS() {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		Message message = Message.creator(
				new com.twilio.type.PhoneNumber("+2347062931318"), 
				new com.twilio.type.PhoneNumber("+12057844773"), 
				"Testing twilio")
				.create();
		return message;
	}

}
