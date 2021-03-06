package com.banka.services;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.SimpleMailMessage;
import com.banka.exceptions.CredentialAlreadyInUseException;
import com.banka.exceptions.CredentialNotFoundException;
import com.banka.exceptions.EmailAlreadyInUseException;
import com.banka.exceptions.EmailSendingException;
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
import com.banka.model.UserStatus;
import com.banka.payloads.AccountInfoResponse;
import com.banka.payloads.ChangePinRequest;
import com.banka.payloads.MakeDepositPayload;
import com.banka.payloads.Operation;
import com.banka.payloads.PasswordResetRequest;
import com.banka.payloads.PinResetRequest;
import com.banka.payloads.RegistrationSuccessResponse;
import com.banka.payloads.TransactionDto;
import com.banka.payloads.TransferRequestPayload;
import com.banka.payloads.TransferSuccessResponse;
import com.banka.payloads.UserRegPayload;
import com.banka.payloads.WithdrawalRequestPayload;
import com.banka.repositories.AdminProfileRepository;
import com.banka.repositories.PasswordResetRepository;
import com.banka.repositories.PinResetRepository;
import com.banka.repositories.RoleRepository;
import com.banka.repositories.TransactionRepository;
import com.banka.repositories.UserProfileRepository;
import com.banka.repositories.UserRepository;
import static com.banka.utils.Constants.*;
import static com.banka.model.UserStatus.*;
import static com.banka.utils.GenTransactionId.*;
import static com.banka.model.RoleName.*;

@Service
@Transactional
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
		
		String transferPin = generateTransferPin();
		User newUser = new User(userRegPayload.getFullname(), userRegPayload.getSex(), userRegPayload.getUsername(), userRegPayload.getEmail(), 
				passwordEncoder.encode(userRegPayload.getPassword()), passwordEncoder.encode(transferPin));
		
		if(userRegPayload.getRole() == null) {
			userRegPayload.setRole(ROLE_USER.name());
		}
		
		Role userRole = assignRole(userRegPayload);
		
		if (userRole == null) {
			throw new CredentialNotFoundException("role not found");
		}
		
		newUser.setRoles(Collections.singleton(userRole));
		
		
		
		if(userRegPayload.getRole().equalsIgnoreCase(ROLE_CASHIER.name()) || userRegPayload.getRole().equalsIgnoreCase(ROLE_ADMIN.name())) {
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
			UserProfile userProfile = new UserProfile(phoneNumber, accountNumber);
			userProfile.setUser(newUser);
			
			// create PinReset credentials for activation
			String generatedToken = generateResetToken();
			// send activation mail
			sendMailForAccountActivation(newUser, transferPin, httpServletRequest, generatedToken);
			userRepo.save(newUser);
			userProfileRepo.save(userProfile);
			//newUser.setUserProfile(userProfile);
			//smsService.sendSMS(userRegPayload.getFullname(), phoneNumber, accountNumber);
			
			
			createPinResetToken(newUser, generatedToken);
			
			// create registration bonus 
			createRegistrationBonus(userProfile);
			// send activation mail
//				sendMailForAccountActivation(newUser, transferPin, httpServletRequest, generatedToken);
		
		}
		
		return newUser;
	}
	
	@Override
	public RegistrationSuccessResponse confirmRegistration(String confirmationToken) {
		if(confirmationToken == null) {
			throw new InvalidCredentialException("invalid token or something went wrong, your registration is not confirmed!");
		}
		PinReset pinReset = pinResetRepo.getByResetToken(confirmationToken);
		if(pinReset == null) {
			throw new InvalidCredentialException("invalid token or something went wrong, your registration is not confirmed!");
		}
		
		User user = pinReset.getUser();
		user.setIsActive(DEFAULT_PIN_NOT_CHANGED.name());
		userRepo.save(user);
		RegistrationSuccessResponse regSuccessResponse = new RegistrationSuccessResponse(user.getFullname());
				
		return regSuccessResponse;
	}
	
	@Override
	public void changePin(@Valid ChangePinRequest changePinRequest, String username) {
		User user = userRepo.getByUsername(username);
		if(user == null) {
			throw new InvalidCredentialException("invalid credential supplied"); 
		}
		if(!(user.getIsActive().equalsIgnoreCase(ACTIVE.name()) || user.getIsActive().equalsIgnoreCase(DEFAULT_PIN_NOT_CHANGED.name()))) {
			informUser(user.getIsActive());
		}
		
//		UserProfile userProfile = userProfileRepo.getUserProfileByUserId(user.getId());
//		if(userProfile == null) {
//			throw new InvalidCredentialException("invalid credential supplied"); 
//		}
		
//		if(!passwordEncoder.matches(changePinRequest.getCurrentPin(), userProfile.getTransferPin())) {
//			throw new InvalidCredentialException("invalid pin");
//		}
		
		comparePins(changePinRequest.getCurrentPin(), user.getTransferPin());
		
		// pin must be 4 to 8 digits
		if (!changePinRequest.getConfirmNewPin().matches("\\d{4,8}")) {
			throw new InvalidCredentialException("new pin must be all digits and 4 to 8 digits long");
		}
		
		
		user.setTransferPin(passwordEncoder.encode(changePinRequest.getNewPin()));
		userRepo.save(user);
		
		if(user.getIsActive().equals(DEFAULT_PIN_NOT_CHANGED.name())) {
			user.setIsActive(ACTIVE.name());
			userRepo.save(user);
		}
		
	}
	
	@Override
	public void resetPin(@Valid ChangePinRequest changePinRequest, String username) {
		User user = userRepo.getByUsername(username);
		
		if(user == null) {
			throw new InvalidCredentialException("invalid credential supplied"); 
		}
		if(!(user.getIsActive().equalsIgnoreCase(ACTIVE.name()))) {
			informUser(user.getIsActive());
		}
		
		PinReset pinReset = pinResetRepo.getByUserId(user.getId());
		String  encodedToken = pinReset.getResetToken();
		
//		UserProfile userProfile = userProfileRepo.getUserProfileByUserId(user.getId());
//		if(userProfile == null) {
//			throw new InvalidCredentialException("invalid credential supplied"); 
//		}
		
//		if(!passwordEncoder.matches(changePinRequest.getCurrentPin(), userProfile.getTransferPin())) {
//			throw new InvalidCredentialException("invalid pin");
//		}
		
		comparePins(changePinRequest.getCurrentPin(), encodedToken);
		
		// pin must be 4 to 8 digits
		if (!changePinRequest.getConfirmNewPin().matches("\\d{4,8}")) {
			throw new InvalidCredentialException("new pin must be all digits and 4 to 8 digits long");
		}
		
		
		user.setTransferPin(passwordEncoder.encode(changePinRequest.getNewPin()));
		userRepo.save(user);
		pinResetRepo.delete(pinReset);
		
	}
	
	
	@Override
	public TransferSuccessResponse makeTransfer(TransferRequestPayload transferRequestPayload, String name) {
		verifyBeneficiaryAccountNumber(transferRequestPayload.getBenfAcctNum());
		String transferAmount =  verifyTransferFund(transferRequestPayload.getAmount());
		transferRequestPayload.setAmount(transferAmount);
		
		UserProfile beneficiary = userProfileRepo.getByAccountNumber(transferRequestPayload.getBenfAcctNum());
		User sender = userRepo.getByUsername(name);
		
		if(beneficiary == null) {
			throw new InvalidCredentialException("beneficiary's account number does not exist");
		}
		
		if(sender == null) {
			throw new InvalidCredentialException("invalid user");
		}
		
		 
		if(!sender.getIsActive().equalsIgnoreCase(ACTIVE.name())) {
			informUser(sender.getIsActive());
		}
		
		
		
		// compare pins -- to be moved
//		if(!passwordEncoder.matches(transferRequestPayload.getPin(), userProfile.getTransferPin())) {
//			throw new InvalidCredentialException("invalid pin supplied");
//		}
		
		comparePins(transferRequestPayload.getPin(), sender.getTransferPin());

		
		//BigDecimal transferCharges = TRANSFER_CHARGE; 
		UserProfile senderUserProfile = userProfileRepo.getUserProfileByUserId(sender.getId());
		BigDecimal senderAccountBalance = senderUserProfile.getAccountBalance();
		BigDecimal amountToTransfer = new BigDecimal(transferRequestPayload.getAmount());
		BigDecimal totalDebit = TRANSFER_CHARGE.add(amountToTransfer);
		
		if (senderUserProfile.getAccountNumber().equals(transferRequestPayload.getBenfAcctNum())) {
			throw new InvalidCredentialException("You cannot transfer money to yourself.");
		}
		
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
		String transactionId = generateTransactionId();
		String description = transferRequestPayload.getDescription();
		Transaction senderTransaction = new Transaction(TransactionType.DEBIT.name(), totalDebit, beneficiary.getAccountNumber(), 
				                                          description,null, sender, transactionId);
//		addTransaction(sender, senderTransaction);
		List<Transaction> senderTransactions = sender.getTransactions();
		senderTransactions.add(senderTransaction);
		sender.setTransactions(senderTransactions);
		//userRepo.save(sender);
		
	//	sender.setTransactions(senderTransactions);
		Transaction beneficiaryTransaction = new Transaction(TransactionType.CREDIT.name(), amountToTransfer, senderUserProfile.getAccountNumber(),
				                                description, null, beneficiary.getUser(), transactionId);
		
		User beneficiaryUser = beneficiary.getUser();
	//	addTransaction(beneficiaryUser, beneficiaryTransaction);
		List<Transaction> beneficiaryUserTransactions = beneficiaryUser.getTransactions();
		beneficiaryUserTransactions.add(beneficiaryTransaction);
		//userRepo.save(beneficiaryUser);
		
		// create transfer response object and return it.
//		List<Transaction> senderTransactionz = transactionRepo.getByUserId(sender.getId());
		List<TransactionDto> senderTransactionz = getTransactionsByUserId(sender.getId().toString());
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
		String transactionTime = LocalDateTime.now().format(dtf);
		TransferSuccessResponse transferResponse = new TransferSuccessResponse(
				         sender.getFullname(), beneficiaryUser.getFullname(), amountToTransfer.toString(), transactionId, transactionTime,
				         description,beneficiary.getAccountNumber(), senderUserProfile.getAccountBalance().toString(), senderTransactionz );
		
		return transferResponse;
	}
	
	
	@Override
	@PreAuthorize("hasAnyAuthority('ROLE_CASHIER', 'ROLE_ADMIN')")
	public void makeWithdrawal(@Valid WithdrawalRequestPayload withdrawalRequestPayload, String staffUsername) {
		verifyBeneficiaryAccountNumber(withdrawalRequestPayload.getAcctNum());
		String withdrawalAmt = verifyTransferFund(withdrawalRequestPayload.getAmount());  
		withdrawalRequestPayload.setAmount(withdrawalAmt);
		User staff = userRepo.getByUsername(staffUsername);
		
		if (staff == null) {
			throw new InvalidCredentialException("invalid staff");
		}
		
		comparePins(withdrawalRequestPayload.getPin(), staff.getTransferPin());
		
		BigDecimal minimumWithdrawal = MINIMUM_WITHDRAWAL;
	    UserProfile accountOwner = userProfileRepo.getByAccountNumber(withdrawalRequestPayload.getAcctNum());
		
		if(accountOwner == null) {
			throw new InvalidCredentialException("this account number does not exist");
		}
		
		BigDecimal withdrawalCharges = WITHDRAWAL_CHARGE; 
		BigDecimal ownerAccountBalance = accountOwner.getAccountBalance();
		BigDecimal amountToTransfer = new BigDecimal(withdrawalRequestPayload.getAmount());
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
		String transactionId = generateTransactionId();
		String description = "withdrawal";
		Transaction withdrawalTransaction = new Transaction(TransactionType.DEBIT.name(), totalDebit, withdrawalRequestPayload.getAcctNum(), 
				                                          description,staffUsername,accountOwner.getUser(), transactionId);
		addTransaction(accountOwner.getUser(), withdrawalTransaction);
	
	}


	@Override
	@PreAuthorize("hasAnyAuthority('ROLE_CASHIER', 'ROLE_ADMIN')")
	public void makeDeposit(MakeDepositPayload makeDepositPayload, String staffUsername) {
		verifyBeneficiaryAccountNumber(makeDepositPayload.getAcctNum());
		verifyTransferFund(makeDepositPayload.getAmount());
		
		//BigDecimal minimumDeposit = new BigDecimal("100");
		UserProfile beneficiary = userProfileRepo.getByAccountNumber(makeDepositPayload.getAcctNum());
		
		if(beneficiary == null) {
			throw new InvalidCredentialException("beneficiary's account number does not exist");
		}
		
        User staff = userRepo.getByUsername(staffUsername);
        
		if (staff == null) {
			throw new InvalidCredentialException("invalid staff");
		}
		
		comparePins(makeDepositPayload.getPin(), staff.getTransferPin());
		
		BigDecimal amountToDeposit = new BigDecimal(makeDepositPayload.getAmount());
		
//		if(amountToDeposit.compareTo(minimumDeposit) < 0) {
//			 throw new InsufficientFundException("minimum deposit is " + minimumDeposit);
//		}
		
		BigDecimal newAccountBal = beneficiary.getAccountBalance().add(amountToDeposit);
		beneficiary.setAccountBalance(newAccountBal);
		userProfileRepo.save(beneficiary);
		
		String transactionId = generateTransactionId();
		String description = "deposit";
		Transaction depositTransaction = new Transaction(TransactionType.CREDIT.name(), amountToDeposit, makeDepositPayload.getAcctNum(), 
				                                          description,staffUsername, beneficiary.getUser(), transactionId);
		addTransaction(beneficiary.getUser(), depositTransaction);
		
	}
	
	@Override
	public String userStatusOperation(Operation operation, String username) {
		 checkIfAccountNumberExists(operation.getAcctNum());
		 User staff = userRepo.getByUsername(username);
		// comparePins(operation.getPin(), staff.getTransferPin());
		 boolean isValidUserStatus = checkIfUserStatusExist(operation.getStatus());
		
		 if (!isValidUserStatus) {
			 throw new InvalidCredentialException("Invalid user status supplied");
		 }
		 
		  // fetch user by account number
		 User user = userProfileRepo.getByAccountNumber(operation.getAcctNum()).getUser();
		  // setIsActive to what user admin supplies
		 user.setIsActive(operation.getStatus());
		 userRepo.save(user);
		return "Successful";
	}
	
	@Override
	public Page<Transaction> findTransactionsByDateRange(String start, String end, Pageable pageable) {
		if(start.length() < 1 || end.length() < 1) {
			throw new InvalidCredentialException("Start date or end date cannot be empty");
		}
		
		LocalDate startDate = LocalDate.parse(start);
		LocalDate endDate = LocalDate.parse(end);
		if(startDate.isAfter(endDate)) {
			throw new InvalidCredentialException("Start date cannot be greater than end date");
		}
		
		String endDayStr = endDate.plusDays(1).toString();
		Page<Transaction> transactions = transactionRepo.findByDateRange(start, endDayStr, pageable);
		return transactions;
	}
	
	private void comparePins(String suppliedCurrentPin, String currentPin) {
		if(!passwordEncoder.matches(suppliedCurrentPin, currentPin)) {
			throw new InvalidCredentialException("invalid pin");
		}
	}
	
	private void addTransaction(User user, Transaction transaction) {
		List<Transaction> userTransactions = user.getTransactions();
		userTransactions.add(transaction);
		user.setTransactions(userTransactions);
		userRepo.save(user);
	}
	
	private void sendMailForAccountActivation(User newUser, String transferPin, HttpServletRequest httpServletRequest, String generatedToken) {
		String appUrl = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName();
		// Email message
		
		SimpleMailMessage mailForActivation = new SimpleMailMessage();
		mailForActivation.setFrom("smartpromise380@gmail.com");
		mailForActivation.setTo(newUser.getEmail());
		mailForActivation.setSubject("Registration Confirmation Request");
		String message = "Congratulations " + newUser.getFullname() + ",\nYour registration was successful, your temporal transfer pin is * " 
		+ transferPin + " * (four digits only without the asterisks)\n You would need to change this pin later so as to be fully activated, but before then we would like you to"
				+ " confirm your registration by clicking the link below:" +
				"\n" + appUrl + ":3000/confirm-registration?token=" + generatedToken;
		mailForActivation.setText(message);
		try {
		emailService.sendEmail(mailForActivation);
		} catch(Exception ex) {
			logger.error("there was an error: " + ex);
			ex.printStackTrace();
			throw new EmailSendingException("Ops! It's not you it's us! An error occurred when sending confirmation mail to " +newUser.getEmail() +
					", it's either you supplied a non-functional mail or the server is down. Please try "
					+ "again later.");	
		}
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
		if(!phoneNumber.matches("0[789]\\d{9}")) {
			throw new InvalidPhoneNumberException("invalid phone number");
		}
		
	}
	
	private void checkIfPhoneNumberExists(String phoneNumber) {
		if(userProfileRepo.existsByPhoneNumber(phoneNumber)) {
			throw new PhoneNumberAlreadyInUseException("phone number already exists, please choose another.");
		}
		
	}
	
	
	private boolean checkIfUserStatusExist(String status) {
		boolean isUserStatus = false;
		for (UserStatus userStatus : UserStatus.values()) {
			if (userStatus.name().equalsIgnoreCase(status)) {
				isUserStatus = true;
				return isUserStatus;
			}
		}
		
		return isUserStatus;
		
	}
	
	
	private void checkIfAccountNumberExists(String accountNumber) {
		if(!userProfileRepo.existsByAccountNumber(accountNumber)) {
			throw new InvalidCredentialException("Invalid account number");
		}
	}
	
	private Role assignRole(UserRegPayload userRegPayload) {
		
		if(userRegPayload.getRole().equalsIgnoreCase(RoleName.ROLE_ADMIN.name())) {
			return roleRepo.findByName(RoleName.ROLE_ADMIN);
		} else if(userRegPayload.getRole().equalsIgnoreCase(RoleName.ROLE_CASHIER.name())) {
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
	public List<String> getTransactionsForThisMonth(String username) {
		Long userId = userRepo.getByUsername(username).getId();
		LocalDateTime ldt = LocalDateTime.now();
		String year = ldt.getYear()+"-";
		String month = ldt.getMonthValue()+"";
		String presentYearAndMonth = year + ""+(ldt.getMonthValue() > 9 ?  month : "0"+month+"%");

	    List<String> createdAtList = transactionRepo.getThisMonthTransactionsDateTime(userId, presentYearAndMonth);
        return createdAtList;
	}

	
	
	private void informUser(String userStatus) {
		if (userStatus.equalsIgnoreCase(REGISTRATION_NOT_CONFIRMED.name())) {
			throw new InvalidCredentialException("Your registration is not confirmed yet, go to your mail and click"
					+ " on the link that was sent to you when you registered to confirm registration.");
		} else if (userStatus.equalsIgnoreCase(DEFAULT_PIN_NOT_CHANGED.name())) {
			throw new InvalidCredentialException("You have not changed your default pin, kindly make use of the "
					+ "change pin link to do so in order to be eligible to make transfer.");
		} else {
			throw new InvalidCredentialException("Your account seems to have been frozen, suspended, deactivated or blocked,"
					+ " kindly contact support for more info .");
		}
	}
	
	@Override
	public User deactivateUser(String username) {
		User user = userRepo.getByUsername(username);
		if(user == null) throw new CredentialNotFoundException("invalid user");
		user.setIsActive(DEACTIVATED.name());
		userRepo.save(user);
		return user;
	}
	
	
	
	@Override
	public User activateUser(String username) {
		User user = userRepo.getByUsername(username);
		if(user == null) throw new CredentialNotFoundException("invalid user");
		user.setIsActive(ACTIVE.name());
		userRepo.save(user);
		return user;
	}
	
	@Override
	public User deactivateCashier(String username) {
		User user = userRepo.getByUsername(username);
		if(user == null) throw new CredentialNotFoundException("invalid cashier");
		user.setIsActive(DEACTIVATED.name());
		userRepo.save(user);
		return user;
	}
	
	
	@Override
	public User activateCashier(String username) {
		User user = userRepo.getByUsername(username);
		if(user == null) throw new CredentialNotFoundException("invalid cashier");
		user.setIsActive(ACTIVE.name());
		userRepo.save(user);
		return user;
	}
	
	
	@Override
	public User getUserByUsernameOrEmail(String usernameOrEmail) {
		User user = userRepo.getByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
		if (user == null) throw new CredentialNotFoundException("invalid user");
		return user;
	}

	public void verifyBeneficiaryAccountNumber(String accountNumber) {
		if(!accountNumber.matches("0\\d{9}")) {
			throw new InvalidCredentialException("invalid beneficiary account number");
		}
		
	}
	
	public String verifyTransferFund(String transferAmount) {
		transferAmount = transferAmount.replaceAll(",", "");
		transferAmount = transferAmount.replaceAll(" ", "");
		
		// transfer amount must be all digits but may or may not contain decimals
		if(!transferAmount.matches("\\d+(\\.\\d{2})?")) {
			throw new InvalidCredentialException("invalid fund - fund must be all digits");
		}
		
		// if transfer amount does not contain decimal append it
		if (!transferAmount.matches(".+\\.\\d{2}")) {
					transferAmount += ".00";
		}
		
		return transferAmount;
	}

    
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
		passwordResetEmail.setFrom("tapp1903@gmail.com");
		passwordResetEmail.setTo(passwordResetRequest.getEmail());
		passwordResetEmail.setSubject("Password Reset Request");
		String message = "Hi " + user.getFullname() + ",\n Someone requested to reset your password, if it wasn't you kindly ignore this"
				+ " message, your account is safe with us.\n If it was you kindly click the link below:\n" + appUrl + ":3000/password-reset?token=" + generatedToken;
		passwordResetEmail.setText(message);
		try {
		emailService.sendEmail(passwordResetEmail);
		} catch (Exception ex) {
			logger.error("there was an error: " + ex);
			ex.printStackTrace();
			throw new EmailSendingException("Ops! It's not you it's us! An error occurred when sending password reset mail to " +passwordResetRequest.getEmail() +
					", it's either you supplied a non-functional mail or the server is down. Please try "
					+ "again later.");	
		}

		
	}
	
	@Override
	public void processForgotPin(@Valid PinResetRequest pinResetRequest, HttpServletRequest httpServletRequest, String username) {
        User user = userRepo.getByUsername(username);
        String loggedInUserEmail = user.getEmail();
        
		if(!loggedInUserEmail.equalsIgnoreCase(pinResetRequest.getEmail())) {
			throw new InvalidCredentialException("sorry, it appears you do not have an account with us.");
		}
		
		String resetPin = generateTransferPin();
		String encodedResetPin = passwordEncoder.encode(resetPin);
		
		PinReset pinReset = pinResetRepo.getByUserId(user.getId());
		
		// it's possible a user can send pin reset and resend again without using the first one
		// so this handles it
		if(pinReset == null) {
			  createPinResetToken(user, encodedResetPin);
		} else {
			int validityTimeInSeconds = 60 * 30; // 30 minutes
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime expiryDate = now.plusSeconds(validityTimeInSeconds);
			pinReset.setResetToken(encodedResetPin);
			pinReset.setExpiryDate(expiryDate);
			pinResetRepo.save(pinReset);
		}
		
		
		// Email message
		SimpleMailMessage pinResetEmail = new SimpleMailMessage();
		pinResetEmail.setFrom("tapp1903@gmail.com");
		pinResetEmail.setTo(pinResetRequest.getEmail());
		pinResetEmail.setSubject("Pin Reset Request");
		String message = "Hi " + user.getFullname() + ",\n Someone requested to reset your pin, if it wasn't you kindly ignore this"
				+ " message, your account is safe with us.\n If it was you then your reset pin is "  + resetPin;
		pinResetEmail.setText(message);
		try {
		emailService.sendEmail(pinResetEmail);
		} catch (Exception ex) {
			logger.error("there was an error: " + ex);
			ex.printStackTrace();
			throw new EmailSendingException("Ops! It's not you it's us! An error occurred when sending password reset mail to " +pinResetRequest.getEmail() +
					", it's either you supplied a non-functional mail or the server is down. Please try "
					+ "again later.");	
		}
		
	
		
	}
	
	private void createPinResetToken(User user, String generatedToken) {
		int validityTimeInSeconds = 60 * 30 ; // 30 minutes
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expiryDate = now.plusSeconds(validityTimeInSeconds);
		PinReset pinReset = new PinReset(generatedToken, expiryDate, user);
		pinResetRepo.save(pinReset);
	}
	
	@Override
	public AccountInfoResponse getAccountInfo(String username) {
		User user = userRepo.getByUsername(username);
		UserProfile userProfile = userProfileRepo.getUserProfileByUserId(user.getId());
		
		AccountInfoResponse accountInfo = new AccountInfoResponse(userProfile.getAccountNumber(), userProfile.getAccountBalance());
	
		return accountInfo;
	}

	@Override
	public List<UserProfile> getDummyAccounts() {
		List<UserProfile> userProfiles = userProfileRepo.getDummyAccounts();
		return userProfiles;
	}

	@Override
	public List<Transaction> getTransactionByTransId(String transactionId) {
		List<Transaction> transactions = transactionRepo.getByTransactionId(transactionId);
		if (transactions.size() == 0) {
			throw new InvalidCredentialException("No transaction with transaction id " + transactionId);
		}
		return transactions ;
	}
	
	//@Override
	public List<Transaction> getTransactionByStaffInvolved(String staffUsername) {
		List<Transaction> transactions = transactionRepo.getByStaffInvolved(staffUsername);
		if (transactions.size() == 0) {
			throw new InvalidCredentialException("No transaction found for staff with username " + staffUsername);
		}
		return transactions;
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


	private void createRegistrationBonus(UserProfile userProfile) {
		BigDecimal bonus = new BigDecimal("25000.00");
		String transactionId = generateTransactionId();
		userProfile.setAccountBalance(bonus);
		userProfileRepo.save(userProfile);
		Transaction bonusTransaction = new Transaction(TransactionType.REG_BONUS.name(), bonus, userProfile.getAccountNumber(), 
                "registration bonus",null, null, transactionId);
		transactionRepo.save(bonusTransaction);
	}

	private String capitalize(String str)
	{
	    if(str == null) return str;
	    return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}
	
	@Override
	public List<TransactionDto> getTransactionsByUserId(String userId) {
		Long id = null;
		try {
			id = Long.parseLong(userId);
		} catch (Exception ex) {
			throw new InvalidCredentialException("id must be digits");
		}
		
		List<Transaction> transactionz = transactionRepo.getByUserId(id);
		List<TransactionDto> transactions = new ArrayList<>();
		for (Transaction trans : transactionz) {

			ZonedDateTime ldt = trans.getCreated_At().atZone(ZoneOffset.UTC);
			String meridiem = ldt.getHour() > 12 ? "PM" : "AM";
			String appendZero = ldt.getMinute() > 9 ? "" : "0";
			String created_At = String.format("%s, %s %d %d %d:%s%d %s", capitalize(ldt.getDayOfWeek().toString()), capitalize(ldt.getMonth().toString()), ldt.getDayOfMonth(),
					                               ldt.getYear(), ldt.getHour()%12,appendZero, ldt.getMinute(), meridiem);
		
			TransactionDto transaction = new TransactionDto(trans.getTransactionType(), trans.getAmount(), trans.getAccountNumberInvolved(),
					                       trans.getDescription(), trans.getStaffInvolved(), trans.getTransactionId(), created_At);
			
			transactions.add(transaction);

		}
		return transactions;
	


	}


	
}
