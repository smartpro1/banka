package com.banka.services;

import java.math.BigDecimal;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.banka.exceptions.CredentialAlreadyInUseException;
import com.banka.exceptions.CredentialNotFoundException;
import com.banka.exceptions.InsufficientFundException;
import com.banka.exceptions.InvalidCredentialException;
import com.banka.model.Role;
import com.banka.model.RoleName;
import com.banka.model.User;
import com.banka.payloads.TransferRequestPayload;
import com.banka.payloads.UserRegPayload;
import com.banka.repositories.RoleRepository;
import com.banka.repositories.UserRepository;
import com.banka.security.JwtTokenProvider;

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
	
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
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
		
		//smsService.sendSMS(newUser);
		
		User registeredUser = userRepo.save(newUser);
		
		
		
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
	     
	     // generate 9digits number prepending it with 0 to form 10 digits account number
	     accountNumber = "0" + (int)(Math.random() * (max - min + 1) + min);
		 User user = userRepo.getByAccountNumber(accountNumber);
		 
		 // ensures account number is unique else it will be regenerated
		 if(user == null) break;
		}
		
		return accountNumber;
	}


	@Override
	public void makeTransfer(TransferRequestPayload transferRequestPayload, String name) {
		
		verifyBeneficiaryAccountNumber(transferRequestPayload.getAccountNumber());
		verifyTransferFund(transferRequestPayload.getTransferAmount());
		
		User beneficiary = userRepo.getByAccountNumber(transferRequestPayload.getAccountNumber());
		User sender = userRepo.getByUsername(name);
		
		if(beneficiary == null) {
			throw new InvalidCredentialException("beneficiary's account number does not exist");
		}
		
		if(name.length() < 5 || sender == null) {
			throw new InvalidCredentialException("invalid user");
		}
		
		BigDecimal transferCharges = getTransferCharges(); 
		BigDecimal senderAccountBalance = sender.getAccountBalance();
		BigDecimal amountToTransfer = new BigDecimal(transferRequestPayload.getTransferAmount());
		BigDecimal totalDebit = transferCharges.add(amountToTransfer);
		
		if(senderAccountBalance.compareTo(amountToTransfer) < 0) {
			 throw new InsufficientFundException("insufficient fund");
		}
		
		senderAccountBalance = senderAccountBalance.subtract(totalDebit);
		sender.setAccountBalance(senderAccountBalance);
		BigDecimal beneficiaryNewAcctBal = beneficiary.getAccountBalance().add(amountToTransfer);
		beneficiary.setAccountBalance(beneficiaryNewAcctBal);
		
		
		// send twilio notification to sender
		
		//smsService.sendSMS(sender, "debit", amountToTransfer, beneficiary.getAccountNumber());
		
		// send twilio notification to beneficiary
		//smsService.sendSMS(beneficiary, "credit", amountToTransfer, sender.getAccountNumber());
		
		userRepo.save(sender);
		userRepo.save(beneficiary);
		
	}


	public void verifyBeneficiaryAccountNumber(String accountNumber) {
		if(!accountNumber.startsWith("0") || accountNumber.length() != 10 || !accountNumber.matches("[0-9]+")) {
			throw new InvalidCredentialException("invalid beneficiary account number");
		}
		
	}
	
	private void verifyTransferFund(String transferAmount) {
		if(!transferAmount.matches("[0-9]+")) {
			throw new InvalidCredentialException("invalid fund - fund must be all digits");
		}
		
	}
	
	@Override
	public BigDecimal getTransferCharges() {
		String transferFee = "25.00";
		BigDecimal transferCharges = new BigDecimal(transferFee);
		return transferCharges;
	}

}
