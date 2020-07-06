package com.banka.validators;

import org.springframework.validation.Errors;

import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

import com.banka.payloads.ChangePasswordRequest;



@Component
public class ChangePasswordValidator implements Validator{
	
	@Override
	public boolean supports(Class<?> clazz) {
		return ChangePasswordRequest.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ChangePasswordRequest changePasswordRequest = (ChangePasswordRequest) target;
		if(changePasswordRequest.getPassword().length() < 6) {
			errors.rejectValue("password", "Length", "must be at least 6 characters");
		}
		
		if(!changePasswordRequest.getPassword().equals(changePasswordRequest.getConfirmPassword())) {
			errors.rejectValue("confirmPassword", "Match", "passwords must match");
		}
		
	}

}
