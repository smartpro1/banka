package com.banka.services;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.banka.model.User;
import com.banka.model.UserProfile;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

@Service
public class SMSServiceImpl implements SMSService {

	public static final String ACCOUNT_SID = "AC49fb855ca34110be1390b8b7948e36d1";
	public static final String AUTH_TOKEN = "9f502e21a2814083e60949e3ba752c17";
	public static final String TWILIO_PHONE = "+12057844773";
	
	
	@Override
	public Message sendSMS(String fullname, String phoneNumber, String accountNumber) {

		String output = String.format("Dear %s, your account registration was successful and your account number is %s, login to update your profile", 
				                 fullname, accountNumber);
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		return createTwilioMesg(phoneNumber, TWILIO_PHONE, output);
	}


	@Override
	public Message sendSMS(UserProfile userProf, String transactionType, BigDecimal transferAmount, String transAcctNum) {
		String direction = "To";
		
		if(transactionType.equals("credit")) {
			direction = "From";
		}
		
		String directionAcct = transAcctNum.substring(0, 3) + "*******" + transAcctNum.substring(8);
		
		String date = LocalDateTime.now().toString();
		String day = date.substring(0, 10);
		String time = date.substring(11, 19);
		DecimalFormat df = new DecimalFormat("#, ###.00");
		String amount = df.format(transferAmount); 
		String availBal = df.format(userProf.getAccountBalance());
		String output = String.format("\n%s\nAmount:NGN%s\n%s:%s\nTime:%s @ %s\nAvail Bal:NGN%s", transactionType,
				             amount, direction, directionAcct, day, time, availBal);
		
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		return createTwilioMesg(userProf.getPhoneNumber(), TWILIO_PHONE, output);
	}
	
	public Message createTwilioMesg(String userPhone, String twilioPhone, String displayMesg) {
		String modifiedUserPhone = "+234" + userPhone;
		Message message = Message.creator(
				new com.twilio.type.PhoneNumber(modifiedUserPhone), 
				new com.twilio.type.PhoneNumber(twilioPhone), 
				displayMesg)
				.create();
		return message;
	}

}
