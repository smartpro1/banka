package com.banka.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.banka.payloads.UserRegPayload;

@Component
public class AppValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return UserRegPayload.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		UserRegPayload userRegPayload = (UserRegPayload) object;
		if(userRegPayload.getPassword().length() < 6) {
			errors.rejectValue("password", "Length", "Password must be at least 6 characters");
		}
		
		if(!userRegPayload.getPassword().equals(userRegPayload.getConfirmPassword())) {
			errors.rejectValue("confirmPassword", "Match", "passwords must match");
		}
		
	}
	

}
