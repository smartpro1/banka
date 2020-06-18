package com.banka.services;

import java.math.BigDecimal;
import java.util.Collections;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.banka.exceptions.CredentialAlreadyInUseException;
import com.banka.exceptions.CredentialNotFoundException;
import com.banka.exceptions.InsufficientFundException;
import com.banka.exceptions.InvalidCredentialException;
import com.banka.model.AdminProfile;
import com.banka.model.Role;
import com.banka.model.RoleName;
import com.banka.model.User;
import com.banka.model.UserProfile;
import com.banka.payloads.TransferRequestPayload;
import com.banka.payloads.UserRegPayload;
import com.banka.repositories.RoleRepository;
import com.banka.repositories.UserProfileRepository;
import com.banka.repositories.UserRepository;


@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private UserProfileRepository userProfileRepo;
	
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private SMSService smsService;
	
	//private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Override
	public User registerUser(UserRegPayload userRegPayload) {
		checkIfUsernameOrEmailExists(userRegPayload);
		
		User newUser = new User(userRegPayload.getFullname(), userRegPayload.getSex(), userRegPayload.getUsername(), userRegPayload.getEmail(), 
				passwordEncoder.encode(userRegPayload.getPassword()));
		
		Role userRole = assignRole(userRegPayload);
		
		if (userRole == null) throw new CredentialNotFoundException("role not found");
		
		newUser.setRoles(Collections.singleton(userRole));
		
		if(userRegPayload.getRole().equalsIgnoreCase("cashier") || userRegPayload.getRole().equalsIgnoreCase("admin")) {
			String staffRole = userRegPayload.getRole();
			AdminProfile adminProfile = new AdminProfile(staffRole);
			adminProfile.setAdmin(newUser);
			newUser.setAdminProfile(adminProfile);
		}else {
			String phoneNumber = userRegPayload.getPhoneNumber();
			verifyPhoneNumber(phoneNumber);
			checkIfPhoneNumberExists(phoneNumber);
			String accountNumber = generateAccountNumber();
			UserProfile userProfile = new UserProfile(phoneNumber, accountNumber);
			userProfile.setUser(newUser);
			newUser.setUserProfile(userProfile);
			//smsService.sendSMS(userRegPayload.getFullname(), phoneNumber, accountNumber);
		}
		
		User registeredUser = userRepo.save(newUser);
		
		
		
		return registeredUser;
	}
	

	private void checkIfUsernameOrEmailExists(UserRegPayload userRegPayload) {
		
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
	
	private void checkIfPhoneNumberExists(String phoneNumber) {
		if(userProfileRepo.existsByPhoneNumber(phoneNumber)) {
			throw new CredentialAlreadyInUseException("phone number already exists, please choose another.");
		}
		
	}
	
	private Role assignRole(UserRegPayload userRegPayload) {
		
		if(userRegPayload.getRole().equalsIgnoreCase("admin")) {
			return roleRepo.findByName(RoleName.ROLE_ADMIN);
		} else if(userRegPayload.getRole().equalsIgnoreCase("cashier")) {
			return roleRepo.findByName(RoleName.ROLE_CASHIER);
		}else {
			return roleRepo.findByName(RoleName.ROLE_USER);
		}
	}
	
	private String generateAccountNumber() {
		String accountNumber = "";
		
		while (true) {
		 int max = 999999999;
	     int min = 100000000;
	     
	     // generates 9digits number prepending it with 0 to form 10 digits account number
	     accountNumber = "0" + (int)(Math.random() * (max - min + 1) + min);
		 UserProfile userProfile = userProfileRepo.getByAccountNumber(accountNumber);
		 
		 // ensures account number is unique else it will be regenerated
		 if(userProfile == null) break;
		}
		
		return accountNumber;
	}


	@Override
	public void makeTransfer(TransferRequestPayload transferRequestPayload, String name) {
		
		verifyBeneficiaryAccountNumber(transferRequestPayload.getAccountNumber());
		verifyTransferFund(transferRequestPayload.getTransferAmount());
		
		UserProfile beneficiary = userProfileRepo.getByAccountNumber(transferRequestPayload.getAccountNumber());
		User sender = userRepo.getByUsername(name);
		
		if(beneficiary == null) {
			throw new InvalidCredentialException("beneficiary's account number does not exist");
		}
		
		if(name.length() < 5 || sender == null) {
			throw new InvalidCredentialException("invalid user");
		}
		
		BigDecimal transferCharges = getTransferCharges(); 
		BigDecimal senderAccountBalance = sender.getUserProfile().getAccountBalance();
		BigDecimal amountToTransfer = new BigDecimal(transferRequestPayload.getTransferAmount());
		BigDecimal totalDebit = transferCharges.add(amountToTransfer);
		
		if(senderAccountBalance.compareTo(amountToTransfer) < 0) {
			 throw new InsufficientFundException("insufficient fund");
		}
		
		senderAccountBalance = senderAccountBalance.subtract(totalDebit);
		sender.getUserProfile().setAccountBalance(senderAccountBalance);
		BigDecimal beneficiaryNewAcctBal = beneficiary.getAccountBalance().add(amountToTransfer);
		beneficiary.setAccountBalance(beneficiaryNewAcctBal);
		
		
		// send twilio notification to sender
		
		//smsService.sendSMS(sender, "debit", amountToTransfer, beneficiary.getAccountNumber());
		
		// send twilio notification to beneficiary
		//smsService.sendSMS(beneficiary, "credit", amountToTransfer, sender.getAccountNumber());
		
		userRepo.save(sender);
		//userProfileRepo.save(beneficiary);
		
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
	
	
	
	@Override
	public User deactivateUser(String username) {
		User user = userRepo.getByUsername(username);
		if(user == null) throw new CredentialNotFoundException("invalid user");
		user.setIsActive((byte) 0);
		userRepo.save(user);
		return user;
	}
	
	
	
	@Override
	public User activateUser(String username) {
		User user = userRepo.getByUsername(username);
		if(user == null) throw new CredentialNotFoundException("invalid user");
		user.setIsActive((byte) 1);
		userRepo.save(user);
		return user;
	}
	
	@Override
	public User deactivateCashier(String username) {
		User user = userRepo.getByUsername(username);
		if(user == null) throw new CredentialNotFoundException("invalid cashier");
		user.setIsActive((byte) 0);
		userRepo.save(user);
		return user;
	}
	
	
	@Override
	public User activateCashier(String username) {
		User user = userRepo.getByUsername(username);
		if(user == null) throw new CredentialNotFoundException("invalid cashier");
		user.setIsActive((byte) 1);
		userRepo.save(user);
		return user;
	}
	
	
	@Override
	public User getUserByUsernameOrEmailOrPhone(String usernameOrEmailOrPhone) {
		User user = findByUsernameOrEmailOrPhone(usernameOrEmailOrPhone);
		return user;
	}


	private User findByUsernameOrEmailOrPhone(String usernameOrEmailOrPhone) {
		User user = userRepo.getByUsername(usernameOrEmailOrPhone);
		if (user == null) {
			user = userRepo.getByEmail(usernameOrEmailOrPhone);
		}
		
		if(user == null) {
			user = userRepo.getByPhone(usernameOrEmailOrPhone);
			if (user == null) throw new CredentialNotFoundException("invalid user");
		}
		
		return user;
	}
	
	
	
	
	
	
	
	

}
