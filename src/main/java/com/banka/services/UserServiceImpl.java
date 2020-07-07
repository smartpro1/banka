package com.banka.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.SimpleMailMessage;
import com.banka.exceptions.CredentialAlreadyInUseException;
import com.banka.exceptions.CredentialNotFoundException;
import com.banka.exceptions.EmailAlreadyInUseException;
import com.banka.exceptions.InsufficientFundException;
import com.banka.exceptions.InvalidCredentialException;
import com.banka.exceptions.InvalidPhoneNumberException;
import com.banka.exceptions.PhoneNumberAlreadyInUseException;
import com.banka.exceptions.UsernameAlreadyInUseException;
import com.banka.model.AdminProfile;
import com.banka.model.PasswordReset;
import com.banka.model.PinReset;
import com.banka.model.Role;
import com.banka.model.RoleName;
import com.banka.model.Transaction;
import com.banka.model.TransactionType;
import com.banka.model.User;
import com.banka.model.UserProfile;
import com.banka.payloads.MakeDepositPayload;
import com.banka.payloads.PasswordResetRequest;
import com.banka.payloads.TransferRequestPayload;
import com.banka.payloads.UserRegPayload;
import com.banka.payloads.WithdrawalRequestPayload;
import com.banka.repositories.AdminProfileRepository;
import com.banka.repositories.PasswordResetRepository;
import com.banka.repositories.PinResetRepository;
import com.banka.repositories.RoleRepository;
import com.banka.repositories.TransactionRepository;
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
	private AdminProfileRepository adminProfileRepo;
	
	@Autowired
	private PasswordResetRepository passwordResetRepo;
	
	@Autowired
	private PinResetRepository pinResetRepo;
	
	@Autowired
	private TransactionRepository transactionRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private SMSService smsService;
	
	@Autowired
	private EmailService emailService;
	
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Override
	public User registerUser(UserRegPayload userRegPayload, HttpServletRequest httpServletRequest) {
		checkIfUsernameOrEmailExists(userRegPayload);
		
		User newUser = new User(userRegPayload.getFullname(), userRegPayload.getSex(), userRegPayload.getUsername(), userRegPayload.getEmail(), 
				passwordEncoder.encode(userRegPayload.getPassword()));
		
		if(userRegPayload.getRole() == null) userRegPayload.setRole("user");
		Role userRole = assignRole(userRegPayload);
		
		if (userRole == null) throw new CredentialNotFoundException("role not found");
		
		newUser.setRoles(Collections.singleton(userRole));
		
		
		
		if(userRegPayload.getRole().equalsIgnoreCase("cashier") || userRegPayload.getRole().equalsIgnoreCase("admin")) {
			String staffRole = userRegPayload.getRole();
			AdminProfile adminProfile = new AdminProfile(staffRole);
			adminProfile.setAdmin(newUser);
			userRepo.save(newUser);
			adminProfileRepo.save(adminProfile);
			//newUser.setAdminProfile(adminProfile);
		}else {
			String phoneNumber = userRegPayload.getPhoneNumber();
			verifyPhoneNumber(phoneNumber);
			checkIfPhoneNumberExists(phoneNumber);
			String accountNumber = generateAccountNumber();
			String transferPin = generateTransferPin();
			UserProfile userProfile = new UserProfile(phoneNumber, accountNumber, passwordEncoder.encode(transferPin));
			userProfile.setUser(newUser);
			 userRepo.save(newUser);
			userProfileRepo.save(userProfile);
			//newUser.setUserProfile(userProfile);
			//smsService.sendSMS(userRegPayload.getFullname(), phoneNumber, accountNumber);
			
			// create PinReset credentials for activation
			String generatedToken = generateResetToken();
			int validityTimeInSeconds = 60 * 60 * 1/2; // 30 minutes
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime expiryDate = now.plusSeconds(validityTimeInSeconds);
			PinReset pinReset = new PinReset(generatedToken, expiryDate, userProfile);
			pinResetRepo.save(pinReset);
			
			// send activation mail
			try {
				sendMailForAccountActivation(newUser, transferPin, httpServletRequest, generatedToken);
			} catch(Exception ex) {
				logger.error("there was an error: " + ex);
			}
		}
		
		return newUser;
	}
	
	private void sendMailForAccountActivation(User newUser, String transferPin, HttpServletRequest httpServletRequest, String generatedToken) {
		String appUrl = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName();
		// Email message
		SimpleMailMessage mailForActivation = new SimpleMailMessage();
		mailForActivation.setFrom("smartpromise380@gmail.com");
		mailForActivation.setTo(newUser.getEmail());
		mailForActivation.setSubject("Password Reset Request");
		String message = "Congratulations " + newUser.getFullname() + ",\n  Your registration was successful, your temporal transfer pin is " + transferPin 
				+ ".\nYou need to change this pin so as to be activated, kindly click the link below so as to reset your"
				+ "transfer pin to what you want.\n" + appUrl + ":3000/password-reset?token=" + generatedToken;
		mailForActivation.setText(message);
		emailService.sendEmail(mailForActivation);
	}
	

	private void checkIfUsernameOrEmailExists(UserRegPayload userRegPayload) {
		
		if(userRepo.existsByUsername(userRegPayload.getUsername())) {
			throw new UsernameAlreadyInUseException("username already exists, please choose another.");
		}
		
		if(userRepo.existsByEmail(userRegPayload.getEmail())) {
			throw new EmailAlreadyInUseException("email already taken, kindly choose another.");
		}
		
	}
	
	private void verifyPhoneNumber(String phoneNumber) {
		if(!phoneNumber.startsWith("0") || phoneNumber.length() != 11 || !phoneNumber.matches("[0-9]+")) {
			throw new InvalidPhoneNumberException("invalid phone number");
		}
		
	}
	
	private void checkIfPhoneNumberExists(String phoneNumber) {
		if(userProfileRepo.existsByPhoneNumber(phoneNumber)) {
			throw new PhoneNumberAlreadyInUseException("phone number already exists, please choose another.");
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
	
	// generate 4 digits transfer pin in String format
	private String generateTransferPin() {
			 int max = 1000;
		     int min = 9999;
		     
		     // generates 9digits number prepending it with 0 to form 10 digits account number
		     String transferPin = "" + (int)(Math.random() * (max - min + 1) + min);
		     return transferPin;
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
		
		if(sender == null) {
			throw new InvalidCredentialException("invalid user");
		}
		
		BigDecimal transferCharges = getTransferCharges(); 
		UserProfile senderUserProfile = userProfileRepo.getUserProfileByUserId(sender.getId());
		BigDecimal senderAccountBalance = senderUserProfile.getAccountBalance();
		BigDecimal amountToTransfer = new BigDecimal(transferRequestPayload.getTransferAmount());
		BigDecimal totalDebit = transferCharges.add(amountToTransfer);
		
		if(senderAccountBalance.compareTo(totalDebit) < 0) {
			 throw new InsufficientFundException("insufficient fund");
		}
		
		senderAccountBalance = senderAccountBalance.subtract(totalDebit);
		senderUserProfile.setAccountBalance(senderAccountBalance);
		BigDecimal beneficiaryNewAcctBal = beneficiary.getAccountBalance().add(amountToTransfer);
		beneficiary.setAccountBalance(beneficiaryNewAcctBal);
		
//		try {
//		 send twilio notification to sender
//		smsService.sendSMS(sender, "debit", amountToTransfer, beneficiary.getAccountNumber());
		//smsService.sendSMS(senderUserProfile, "debit", amountToTransfer, beneficiary.getAccountNumber());
		
//		// send twilio notification to beneficiary
		//smsService.sendSMS(beneficiary, "credit", amountToTransfer, senderUserProfile.getAccountNumber());
//		} catch(Exception ex) {
//			logger.error("error : " + ex);
//		}
//		userRepo.save(sender);
		userProfileRepo.save(senderUserProfile);
		userProfileRepo.save(beneficiary);
		
		
		// create transactions for sender and beneficiary
		
		Transaction senderTransaction = new Transaction(TransactionType.DEBIT, amountToTransfer, beneficiary.getAccountNumber(), 
				                                          transferRequestPayload.getDescription(),null, sender);
		List<Transaction> senderTransactions = sender.getTransactions();
		senderTransactions.add(senderTransaction);
		sender.setTransactions(senderTransactions);
		userRepo.save(sender);
		
		sender.setTransactions(senderTransactions);
		Transaction beneficiaryTransaction = new Transaction(TransactionType.DEBIT, amountToTransfer, senderUserProfile.getAccountNumber(),
				                                  transferRequestPayload.getDescription(), null, beneficiary.getUser());
		User beneficiaryUser = beneficiary.getUser();
		List<Transaction> beneficiaryUserTransactions = beneficiaryUser.getTransactions();
		beneficiaryUserTransactions.add(beneficiaryTransaction);
		userRepo.save(beneficiaryUser);
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
	public User getUserByUsernameOrEmail(String usernameOrEmail) {
		User user = userRepo.getByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
		if (user == null) throw new CredentialNotFoundException("invalid user");
		return user;
	}


	@Override
	public void makeWithdrawal(@Valid WithdrawalRequestPayload withdrawalRequestPayload, String staffUsername) {
		verifyBeneficiaryAccountNumber(withdrawalRequestPayload.getAccountNumber());
		verifyTransferFund(withdrawalRequestPayload.getAmountToWithdraw());
		
		BigDecimal minimumWithdrawal = new BigDecimal("500");
	    UserProfile accountOwner = userProfileRepo.getByAccountNumber(withdrawalRequestPayload.getAccountNumber());
		
		if(accountOwner == null) throw new InvalidCredentialException("this account number does not exist");
		BigDecimal withdrawalCharges = getWithdrawalCharges(); 
		BigDecimal ownerAccountBalance = accountOwner.getAccountBalance();
		BigDecimal amountToTransfer = new BigDecimal(withdrawalRequestPayload.getAmountToWithdraw());
		BigDecimal totalDebit = withdrawalCharges.add(amountToTransfer);
		
		if(amountToTransfer.compareTo(minimumWithdrawal) < 0) {
			 throw new InsufficientFundException("minimum withdrawal is " + minimumWithdrawal);
		}
		
		if(ownerAccountBalance.compareTo(totalDebit) < 0) {
			 throw new InsufficientFundException("insufficient fund");
		}
		
		BigDecimal newAcctBal = ownerAccountBalance.subtract(totalDebit);
		accountOwner.setAccountBalance(newAcctBal);
		userProfileRepo.save(accountOwner);
		
		// Create transaction
		User accountOwnerUser = createTransaction(accountOwner, "debit", staffUsername, amountToTransfer, null);
		userRepo.save(accountOwnerUser);
	}


	@Override
	public void makeDeposit(MakeDepositPayload makeDepositPayload, String staffUsername) {
		verifyBeneficiaryAccountNumber(makeDepositPayload.getAccountNumber());
		verifyTransferFund(makeDepositPayload.getDepositAmount());
		
		BigDecimal minimumDeposit = new BigDecimal("100");
		UserProfile beneficiary = userProfileRepo.getByAccountNumber(makeDepositPayload.getAccountNumber());
		
		if(beneficiary == null) throw new InvalidCredentialException("beneficiary's account number does not exist");
		
		BigDecimal amountToDeposit = new BigDecimal(makeDepositPayload.getDepositAmount());
		
		if(amountToDeposit.compareTo(minimumDeposit) < 0) {
			 throw new InsufficientFundException("minimum deposit is " + minimumDeposit);
		}
		
		BigDecimal newAccountBal = beneficiary.getAccountBalance().add(amountToDeposit);
		beneficiary.setAccountBalance(newAccountBal);
		userProfileRepo.save(beneficiary);
		
		User accountOwnerUser = createTransaction(beneficiary, "credit", staffUsername, amountToDeposit, makeDepositPayload.getDescription());
		userRepo.save(accountOwnerUser);
	}
	
	private User createTransaction(UserProfile userProfile, String trasactionType, String staffUsername, BigDecimal amount,String description) {
		TransactionType transactType = TransactionType.DEBIT;
		if(trasactionType.equals("credit")) transactType = TransactionType.CREDIT;
		Transaction accountOwnerNewTransaction= new Transaction(transactType, amount, null,
				description, staffUsername, userProfile.getUser());
		User accountOwnerUser = userProfile.getUser();
		List<Transaction> accountOwnerTransactions = accountOwnerUser.getTransactions();
		accountOwnerTransactions.add(accountOwnerNewTransaction);
		accountOwnerUser.setTransactions(accountOwnerTransactions);
		return accountOwnerUser;
	}
	
	@Override
	public BigDecimal getWithdrawalCharges() {
		String withdrawalFee = "50.00";
		BigDecimal withdrawalCharges = new BigDecimal(withdrawalFee);
		return withdrawalCharges;
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

    @Transactional
	@Override
	public void processForgotPassword(PasswordResetRequest passwordResetRequest, HttpServletRequest httpServletRequest) {
		User user = userRepo.getByEmail(passwordResetRequest.getEmail());
		
		if(user == null) {
			throw new InvalidCredentialException("sorry, it appears you do not have an account with us.");
		}
		
		
		String generatedToken = generateResetToken();
		int validityTimeInSeconds = 60 * 60 * 1/2; // 30 minutes
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expiryDate = now.plusSeconds(validityTimeInSeconds);
		
		PasswordReset passwordReset = passwordResetRepo.getPasswordResetByUserId(user.getId());
		
		// it's possible a user can send password reset and resend again without using the first one
		// so this handles it
		if(passwordReset == null) {
			 passwordReset = new PasswordReset(generatedToken, expiryDate, user);
		} else {
			passwordReset.setResetToken(generatedToken);
			passwordReset.setExpiryDate(expiryDate);
		}
		
		passwordResetRepo.save(passwordReset);
		
		// something like this : https://mywebapp.com/reset?token=9e5bf4a8-66b8-433e-b91c-6382c1a25f00
		String appUrl = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName();
		// Email message
		SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
		passwordResetEmail.setFrom("smartpromise380@gmail.com");
		passwordResetEmail.setTo(passwordResetRequest.getEmail());
		passwordResetEmail.setSubject("Password Reset Request");
		String message = "Hi " + user.getFullname() + ",\n Someone requested to reset your password, if it wasn't you kindly ignore this"
				+ " message, your account is safe with us.\n If it was you kindly click the link below:\n" + appUrl + ":3000/password-reset?token=" + generatedToken;
		passwordResetEmail.setText(message);
		emailService.sendEmail(passwordResetEmail);

		
	}
	
	public void resetPassword(String password, String token) {
		
		LocalDateTime timeOfReset = LocalDateTime.now();
		PasswordReset passwordReset = passwordResetRepo.findByResetToken(token);
		
		if(passwordReset == null) {
			throw new InvalidCredentialException("invalid or expired token");
		}
		
		User user = passwordReset.getUser();
		
		if( user == null) {
			throw new InvalidCredentialException("invalid user or expired token");
		}
		
		
		
		LocalDateTime expiryDate = passwordReset.getExpiryDate();
		if(timeOfReset.isAfter(expiryDate)) {
			throw new InvalidCredentialException("expired token, please reset password afresh.");
		}
		
	
		user.setPassword(passwordEncoder.encode(password));
		userRepo.save(user);
		passwordResetRepo.deleteById(passwordReset.getId());	
	}
	
	public String generateResetToken() {
		String resetToken = UUID.randomUUID().toString();
		return resetToken;		
	}


	
	
	
	
	
	
	
	

}
