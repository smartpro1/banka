package com.banka.services;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.springframework.stereotype.Service;

import com.banka.model.User;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

@Service
public class SMSServiceImpl implements SMSService {

	public static final String ACCOUNT_SID = "";
	public static final String AUTH_TOKEN = "";
	public static final String TWILIO_PHONE = "";
	
	
	@Override
	public Message sendSMS(User user) {
		String acctNum = user.getAccountNumber();
		String firstName = user.getFirstName();
		String lastName = user.getLastName();
		String phoneNumber = "+234" + user.getPhoneNumber();
		String output = String.format("Dear %s %s, your account registration was successful and your account number is %s, login to update your profile", 
				                 firstName, lastName, acctNum);
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

		return createTwilioMesg(phoneNumber, TWILIO_PHONE, output);
	}


	@Override
	public Message sendSMS(User user, String transactionType, BigDecimal transferAmount, String transAcctNum) {
		String direction = "To";
		
		if(transactionType.equals("credit")) {
			direction = "From";
		}
		
		String directionAcct = transAcctNum.substring(0, 3) + "*******" + transAcctNum.substring(8);
		
		String date = user.getUpdated_At().toString();
		String day = date.substring(0, 10);
		String time = date.substring(11, 19);
		DecimalFormat df = new DecimalFormat("#, ###.00");
		String amount = df.format(transferAmount); 
		String availBal = df.format(user.getAccountBalance());
		String output = String.format("transactionType\nAmount:NGN%s\n%s:%s\nTime:%s @ %s\nAvail Bal:NGN%s", 
				             amount, direction, directionAcct, day, time, availBal);
		
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

		return createTwilioMesg(user.getPhoneNumber(), TWILIO_PHONE, output);
	}
	
	public Message createTwilioMesg(String userPhone, String twilioPhone, String displayMesg) {
		Message message = Message.creator(
				new com.twilio.type.PhoneNumber(userPhone), 
				new com.twilio.type.PhoneNumber(twilioPhone), 
				displayMesg)
				.create();
		return message;
	}

}
