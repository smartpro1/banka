package com.banka.validators;

import org.springframework.validation.Errors;

import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

import com.banka.payloads.ChangePinRequest;



@Component
public class ChangePinValidator implements Validator{
	
	@Override
	public boolean supports(Class<?> clazz) {
		return ChangePinRequest.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ChangePinRequest changePinRequest = (ChangePinRequest) target;
		if(changePinRequest.getNewPin().length() < 4 || changePinRequest.getNewPin().length() > 8) {
			errors.rejectValue("newPin", "Length", "new pin must be four to eight characters");
		}
		
		if(!changePinRequest.getNewPin().equals(changePinRequest.getConfirmNewPin())) {
			errors.rejectValue("confirmNewPin", "Match", "pins must match");
		}
		
	}

}
