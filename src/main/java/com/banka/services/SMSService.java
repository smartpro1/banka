package com.banka.services;

import com.twilio.rest.api.v2010.account.Message;

public interface SMSService {
	public Message sendSMS();
}
