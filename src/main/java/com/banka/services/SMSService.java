package com.banka.services;

import com.banka.model.User;
import com.twilio.rest.api.v2010.account.Message;

public interface SMSService {
	public Message sendSMS(User user);
}
