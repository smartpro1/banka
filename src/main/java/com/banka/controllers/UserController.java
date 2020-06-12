package com.banka.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banka.model.User;
import com.banka.payloads.JwtLoginSuccessResponse;
import com.banka.payloads.UserLoginPayload;
import com.banka.payloads.UserRegPayload;
import com.banka.security.JwtTokenProvider;
import com.banka.services.FieldsValidationService;
import com.banka.services.SMSService;
import com.banka.services.UserService;
import com.banka.validators.AppValidator;
import static com.banka.security.SecurityConstants.TOKEN_PREFIX;


@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FieldsValidationService validateFields;
	
	@Autowired 
	private AppValidator appValidator;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	
	@PostMapping("/register-user")
	public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegPayload userRegPayload, BindingResult result) {
		// compare passwords
		appValidator.validate(userRegPayload, result);
		
		// validate input fields
		ResponseEntity<?> errorMap = validateFields.fieldsValidationService(result);
		if(errorMap != null) return errorMap;
		
		User newUser = userService.registerUser(userRegPayload);
		return new ResponseEntity<User>(newUser, HttpStatus.CREATED);
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginPayload UserLoginRequest, BindingResult result) {
		ResponseEntity<?> errorMap = validateFields.fieldsValidationService(result);
		if(errorMap != null) return errorMap;
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						UserLoginRequest.getUsername(), UserLoginRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = TOKEN_PREFIX + jwtTokenProvider.generateJwtToken(authentication);
		
		return ResponseEntity.ok(new JwtLoginSuccessResponse(true, jwt));
	}
	
	@GetMapping("/test")
	public String test() {
		return "test works";
	}
	
	

}
