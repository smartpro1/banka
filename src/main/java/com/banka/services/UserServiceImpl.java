package com.banka.services;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.banka.exceptions.CredentialAlreadyInUseException;
import com.banka.exceptions.CredentialNotFoundException;
import com.banka.exceptions.InvalidCredentialException;
import com.banka.model.Role;
import com.banka.model.RoleName;
import com.banka.model.User;
import com.banka.payloads.UserRegPayload;
import com.banka.repositories.RoleRepository;
import com.banka.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private SMSService smsService;
	
	@Override
	public User registerUser(UserRegPayload userRegPayload) {
		checkIfUsernameOrEmailOrPhoneExists(userRegPayload);
		
		verifyPhoneNumber(userRegPayload.getPhoneNumber());
		
		User newUser = new User();
		Role userRole = roleRepo.findByName(RoleName.ROLE_USER);
		if (userRole == null) throw new CredentialNotFoundException("role not found");
		
		newUser.setFirstName(userRegPayload.getFirstName());
		newUser.setLastName(userRegPayload.getLastName());
		newUser.setPhoneNumber(userRegPayload.getPhoneNumber());
		newUser.setEmail(userRegPayload.getEmail());
		newUser.setUsername(userRegPayload.getUsername());
		newUser.setPassword(passwordEncoder.encode(userRegPayload.getPassword()));
		newUser.setRoles(Collections.singleton(userRole));
		newUser.setAccountNumber(generateAccountNumber());
		
		
		User registeredUser = userRepo.save(newUser);
		
		//smsService.sendSMS(registeredUser);
		
		return registeredUser;
	}
	

	private void checkIfUsernameOrEmailOrPhoneExists(UserRegPayload userRegPayload) {
		if(userRepo.existsByPhoneNumber(userRegPayload.getPhoneNumber())) {
			throw new CredentialAlreadyInUseException("phone number already exists, please choose another.");
		}
		
		if(userRepo.existsByUsername(userRegPayload.getUsername())) {
			throw new CredentialAlreadyInUseException("username already exists, please choose another.");
		}
		
		if(userRepo.existsByEmail(userRegPayload.getEmail())) {
			throw new CredentialAlreadyInUseException("email already taken, kindly choose another.");
		}
		
	}
	
	private void verifyPhoneNumber(String phoneNumber) {
		if(!phoneNumber.startsWith("0") || phoneNumber.length() != 11 || !phoneNumber.matches("[0-9]+")) {
			throw new InvalidCredentialException("invalid phone number");
		}
		
	}
	
	private String generateAccountNumber() {
		String accountNumber = "";
		
		while (true) {
		 int max = 999999999;
	     int min = 100000000;
	     accountNumber = "0" + (int)(Math.random() * (max - min + 1) + min);
		 User user = userRepo.getByAccountNumber(accountNumber);
		 
		 if(user == null) break;
		}
		
		return accountNumber;
	}

}
