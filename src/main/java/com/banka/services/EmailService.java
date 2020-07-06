package com.banka.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  
	@Autowired
	private JavaMailSender javaMailSender;
	
	public void sendEmail(SimpleMailMessage email) {
//		SimpleMailMessage mail = new SimpleMailMessage();
//		mail.setTo(email);
//		mail.setFrom("support@myprojectManager.com");
//		mail.setSubject("Password Reset");
//		mail.setText(text);
		javaMailSender.send(email);
	}
}
