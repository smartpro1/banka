package com.banka.services;

import static com.banka.model.RoleName.ROLE_CASHIER;
import static com.banka.model.RoleName.ROLE_USER;
import static com.banka.model.UserStatus.ACTIVE;
import static com.banka.model.UserStatus.DEFAULT_PIN_NOT_CHANGED;
import static com.banka.model.UserStatus.REGISTRATION_NOT_CONFIRMED;
import static com.banka.utils.Constants.TRANSFER_CHARGE;
import static com.banka.utils.GenTransactionId.generateTransactionId;
//import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.banka.exceptions.InvalidCredentialException;
import com.banka.model.PinReset;
import com.banka.model.Role;
import com.banka.model.RoleName;
import com.banka.model.Transaction;
import com.banka.model.TransactionType;
import com.banka.model.User;
import com.banka.model.UserProfile;
import com.banka.payloads.ChangePinRequest;
import com.banka.payloads.MakeDepositPayload;
import com.banka.payloads.RegistrationSuccessResponse;
import com.banka.payloads.TransferRequestPayload;
import com.banka.payloads.TransferSuccessResponse;
import com.banka.payloads.UserRegPayload;
import com.banka.payloads.WithdrawalRequestPayload;
import com.banka.repositories.PinResetRepository;
import com.banka.repositories.RoleRepository;
import com.banka.repositories.TransactionRepository;
import com.banka.repositories.UserProfileRepository;
import com.banka.repositories.UserRepository;


//@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ServiceImplTest {
    private Logger logger = LoggerFactory.getLogger(ServiceImplTest.class);
    
	@Autowired
	private UserService userService;
	
	@MockBean
	private UserRepository userRepo;
	
	@MockBean
	HttpServletRequest httpServletRequest;
	
	@MockBean
	private UserProfileRepository userProfileRepo;
	
	@MockBean
	private PinResetRepository pinResetRepo;
	
	@MockBean
	private TransactionRepository transactionRepo;
	
	@MockBean
	private EmailService emailService;
	
	@MockBean
	private RoleRepository roleRepo;
	
	@MockBean
	private PasswordEncoder passwordEncoder;
		
	User user;
	UserRegPayload userRegPayload;
	
	@BeforeEach 
	void init() {
		userRegPayload = new UserRegPayload();
		userRegPayload.setFullname("Akeni Promise");
		userRegPayload.setSex("M");
		userRegPayload.setPhoneNumber("07062916111");
		userRegPayload.setEmail("promise@yahoo.com");
		userRegPayload.setUsername("username");
		userRegPayload.setPassword("password");
		
		user = new User(userRegPayload.getFullname(), userRegPayload.getSex(), userRegPayload.getUsername(), 
				              userRegPayload.getEmail(), userRegPayload.getPassword(), "1234");
		
	}
	
	@Test
	@DisplayName("Register User Test")
	public void shouldReturnRegisteredUserSuccessfully() {
		
		Set<Role> roles = new HashSet<>();
		Role role = new Role(ROLE_USER);
		roles.add(role);
		user.setRoles(roles);
		user.setIsActive("REGISTRATION_NOT_CONFIRMED");
		UserProfile userProfile = new UserProfile("07062916111", "0123456789");
		userProfile.setUser(user);
		
		int validityTimeInSeconds = 60 * 60 * 1/2; // 30 minutes
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expiryDate = now.plusSeconds(validityTimeInSeconds);
		PinReset pinReset = new PinReset(UUID.randomUUID().toString(), expiryDate, user);
		Transaction bonusTransaction = new Transaction(TransactionType.REG_BONUS.name(), new BigDecimal("25000.00"), userProfile.getAccountNumber(), 
                "registration bonus",null, null, "ae12dsff...");
		
		when(userRepo.save(user)).thenReturn(user);
		when(userProfileRepo.save(userProfile)).thenReturn(userProfile);
		when(pinResetRepo.save(pinReset)).thenReturn(pinReset);
		when(transactionRepo.save(bonusTransaction)).thenReturn(bonusTransaction);
		when(roleRepo.findByName(RoleName.ROLE_USER)).thenReturn(role);

		
		User returnedUser = userService.registerUser(userRegPayload, httpServletRequest);
		
		assertTrue(returnedUser.getUsername().equals(user.getUsername()));
		assertTrue(returnedUser.getEmail().equals(user.getEmail()));
	}
	
	@Test
	@DisplayName("Confirm Registration Test")
	public void shouldConfirmRegisteredUser() {
	   String resetToken = UUID.randomUUID().toString();
	   PinReset pinResetObj = new PinReset(resetToken, LocalDateTime.now(),  user);
	   Set<Role> roles = new HashSet<>();
		Role role = new Role(ROLE_USER);
		roles.add(role);
		user.setRoles(roles);
		user.setIsActive("DEFAULT_PIN_NOT_CHANGED");
		
		Mockito.when(pinResetRepo.getByResetToken(resetToken)).thenReturn(pinResetObj);
		
		//RegistrationSuccessResponse regSuccessResponse = new RegistrationSuccessResponse(user.getFullname());
		RegistrationSuccessResponse response = userService.confirmRegistration(resetToken);
		assertTrue(user.getRoles().contains(role));
		assertTrue(user.getIsActive().equals(DEFAULT_PIN_NOT_CHANGED.name()));
		assertTrue(user.getFullname().equals(response.getFullname()));
		
	}
	
	@Test
	@DisplayName("Confirm Registration Error")
	public void shouldThrowErrorWhenResetTokenIsNullOrPinResetNotFound() {
		   String resetToken = null;
		   PinReset pinResetObj = null;
		   Set<Role> roles = new HashSet<>();
		   Role role = new Role(ROLE_USER);
			roles.add(role);
			user.setRoles(roles);
		   user.setIsActive("REGISTRATION_NOT_CONFIRMED");
			
		   Mockito.when(pinResetRepo.getByResetToken(resetToken)).thenReturn(pinResetObj);
		   Exception exception = assertThrows(InvalidCredentialException.class, () ->
		   userService.confirmRegistration(resetToken));
		   
		   assertTrue("invalid token or something went wrong, your registration is not confirmed!".equals(exception.getMessage()));
		   assertTrue(REGISTRATION_NOT_CONFIRMED.name().equals(user.getIsActive()));
    //   assertEquals("invalid token or something went wrong, your registration is not confirmed!", exception.getMessage());
	}
	
	@Test
	@DisplayName("Change Pin Test")
	public void shouldChangePinSuccessfully() {
		Set<Role> roles = new HashSet<>();
		Role role = new Role(ROLE_USER);
		roles.add(role);
		user.setRoles(roles);
		user.setIsActive(ACTIVE.name());
		ChangePinRequest changePinRequest = new ChangePinRequest();
		changePinRequest.setCurrentPin("1234");
		changePinRequest.setNewPin("123456");
		changePinRequest.setConfirmNewPin("123456");
		
		 String encryptedValue = "$2[a$10$EulgXiN/bEwjJZc2IqRgoOyTcJWNZp0STtgY0fZv9XSIWigMHiBN2";
		 Mockito.when(passwordEncoder.encode("1234")).thenReturn(encryptedValue);
		 Mockito.when(passwordEncoder.matches("1234", encryptedValue)).thenReturn(true);
		 Mockito.when(userRepo.getByUsername("username")).thenReturn(user);
		 Mockito.when(userRepo.save(user)).thenReturn(user);
		 user.setTransferPin(passwordEncoder.encode("1234"));
		 
		 userService.changePin(changePinRequest, "username");
		 verify(userRepo, times(1)).save(user);
		 verify(userRepo, times(1)).getByUsername("username");
	}
	
	
//	@Test
//	@DisplayName("Make Transfer Test")
//	public void shouldMakeTransferSuccessfully() {
//		// user makes transfer to user2
//		
//		
//		User user2 = new User("Mike Thompson", "M", "username2", 
//				"mike@yahoo.com", "password", "1234");
//		
//		BigDecimal acctBal = new BigDecimal("25000.00");
//	    UserProfile userProfile = new UserProfile("09062931318", "0212345678");
//		userProfile.setUser(user);
//		userProfile.setAccountBalance(acctBal);
//		
//	    UserProfile userProfile2 = new UserProfile("06062931318", "0212344778");
//		userProfile2.setUser(user2);
//		userProfile2.setAccountBalance(acctBal);
//		
//		TransferRequestPayload request = new TransferRequestPayload();
//		request.setBenfAcctNum(userProfile2.getAccountNumber());
//		request.setAmount("2500");
//		request.setDescription("Commission");
//		request.setPin("1234");
//		
//		String transactionId = generateTransactionId();
//		
//		String encryptedValue = "$2[a$10$EulgXiN/bEwjJZc2IqRgoOyTcJWNZp0STtgY0fZv9XSIWigMHiBN2";
//		Mockito.when(passwordEncoder.encode("1234")).thenReturn(encryptedValue);
//		Mockito.when(passwordEncoder.matches("1234", encryptedValue)).thenReturn(true);
//		Mockito.when(userRepo.getByUsername(user.getUsername())).thenReturn(user);
//		Mockito.when(userRepo.save(user)).thenReturn(user);
//		Mockito.when(userProfileRepo.getByAccountNumber(userProfile2.getAccountNumber())).thenReturn(userProfile2);
//		Mockito.when(userProfileRepo.getUserProfileByUserId(user.getId())).thenReturn(userProfile);
//	//	Mockito.when(generateTransactionId()).thenReturn(transactionId);
//		
//		
//		user.setTransferPin(passwordEncoder.encode("1234"));
//		user.setIsActive("ACTIVE");
//		
//		BigDecimal transferAmt = new BigDecimal(request.getAmount());
//		BigDecimal totalDebit = TRANSFER_CHARGE.add(transferAmt);
//		
//		BigDecimal userAcctBalBeforeTransfer = userProfile.getAccountBalance();
//		BigDecimal user2AcctBalBeforeTransfer = userProfile2.getAccountBalance();
//		
//		userProfile.setAccountBalance(userProfile.getAccountBalance().subtract(totalDebit));
//		userProfile2.setAccountBalance(userProfile2.getAccountBalance().add(transferAmt));
//		
//		BigDecimal userAcctBalAfterTransfer = userProfile.getAccountBalance();
//		BigDecimal user2AcctBalAfterTransfer = userProfile2.getAccountBalance();
//		
//		Transaction userTransaction = new Transaction(TransactionType.DEBIT.name(), totalDebit, userProfile2.getAccountNumber(), 
//                 "Commission",null, user, transactionId);
//		
//		 
//		List<Transaction> userTransactions = new ArrayList<>();
//		userTransactions.add(userTransaction);
//		Mockito.when(transactionRepo.getByUserId(user.getId())).thenReturn(userTransactions);
//		TransferSuccessResponse response = userService.makeTransfer(request, user.getUsername());
//	
//		assertTrue(user.getFullname().equals(response.getSender()));
//		assertEquals(1, response.getTransactions().size());
////		assertNotNull(response);
//		assertTrue(transactionId.contains(response.getTransactionId().substring(13, 24)));
//		
//		Assertions.assertEquals(userAcctBalBeforeTransfer,  userAcctBalAfterTransfer.add(totalDebit));
//		Assertions.assertEquals(user2AcctBalBeforeTransfer.add(transferAmt), user2AcctBalAfterTransfer);
//	}
	
	@Test
	@WithMockUser(username="admin", roles= {"CASHIER", "ADMIN"})
	@DisplayName("Make Withdrawal Test")
	public void shouldMakeWithdrawalSuccessfully() {
		BigDecimal acctBal = new BigDecimal("25000.00");
	    UserProfile userProfile = new UserProfile("08534791318", "0200515678");
		userProfile.setUser(user);
		userProfile.setAccountBalance(acctBal);
		
		User userCashier = new User("Mike Thompson", "M", "username2", 
				"mike@yahoo.com", "password", "1234");
		userCashier.setIsActive("ACTIVE");
		
		Set<Role> roles = new HashSet<>();
		Role role = new Role(ROLE_CASHIER);
		roles.add(role);
		user.setRoles(roles);
		user.setIsActive(ACTIVE.name());
		userCashier.setRoles(roles);
		
		
		WithdrawalRequestPayload withdrawalRequestPayload = new WithdrawalRequestPayload();
		withdrawalRequestPayload.setAcctNum(userProfile.getAccountNumber());
		withdrawalRequestPayload.setAmount("3000");
		withdrawalRequestPayload.setPin(user.getTransferPin());
		
		
		String encryptedValue = "$2[a$10$EulgXiN/bEwjJZc2IqRgoOyTcJWNZp0STtgY0fZv9XSIWigMHiBN2";
		Mockito.when(passwordEncoder.encode("1234")).thenReturn(encryptedValue);
		Mockito.when(passwordEncoder.matches("1234", encryptedValue)).thenReturn(true);
		Mockito.when(userProfileRepo.getByAccountNumber(withdrawalRequestPayload.getAcctNum()))
		                                    .thenReturn(userProfile);
		
		Mockito.when(userRepo.getByUsername(userCashier.getUsername())).thenReturn(userCashier);
		Mockito.when(userProfileRepo.save(userProfile)).thenReturn(userProfile);
		
		userCashier.setTransferPin(passwordEncoder.encode("1234"));
		
		BigDecimal withdrawalAmt = new BigDecimal(withdrawalRequestPayload.getAmount());
		BigDecimal userAcctBalBeforeWithdrawal = userProfile.getAccountBalance();
		userProfile.setAccountBalance(userProfile.getAccountBalance().subtract(withdrawalAmt));
		BigDecimal userAcctBalAfterWithdrawal = userProfile.getAccountBalance();
		
		userService.makeWithdrawal(withdrawalRequestPayload, userCashier.getUsername());
		
		verify(userProfileRepo, times(1)).getByAccountNumber(withdrawalRequestPayload.getAcctNum());
		verify(userRepo, times(1)).getByUsername(userCashier.getUsername());
		verify(userProfileRepo, times(1)).save(userProfile);
		
		Assertions.assertNotEquals(userAcctBalBeforeWithdrawal,  userAcctBalAfterWithdrawal);
		Assertions.assertEquals(userAcctBalAfterWithdrawal,userAcctBalBeforeWithdrawal.subtract(withdrawalAmt));
	}
	
	
	@Test
	@WithMockUser(username="admin", roles= {"CASHIER", "ADMIN"})
	@DisplayName("Make Deposit Test")
	public void shouldMakeDepositSuccessfully() {
		
		BigDecimal acctBal = new BigDecimal("25000.00");
	    UserProfile userProfile = new UserProfile("08582931318", "0298515678");
		userProfile.setUser(user);
		userProfile.setAccountBalance(acctBal);
		
		User userCashier = new User("Mike Thompson", "M", "username2", 
				"mike@yahoo.com", "password", "1234");
		userCashier.setIsActive("ACTIVE");
		
		Set<Role> roles = new HashSet<>();
		Role role = new Role(ROLE_CASHIER);
		roles.add(role);
		user.setRoles(roles);
		user.setIsActive(ACTIVE.name());
		userCashier.setRoles(roles);
		
		
		MakeDepositPayload makeDepositPayload = new MakeDepositPayload();
		makeDepositPayload.setAcctNum(userProfile.getAccountNumber());
		makeDepositPayload.setAmount("3000");
		makeDepositPayload.setDepositor(user.getFullname());
		makeDepositPayload.setPin(user.getTransferPin());
		
		
		String encryptedValue = "$2[a$10$EulgXiN/bEwjJZc2IqRgoOyTcJWNZp0STtgY0fZv9XSIWigMHiBN2";
		Mockito.when(passwordEncoder.encode("1234")).thenReturn(encryptedValue);
		Mockito.when(passwordEncoder.matches("1234", encryptedValue)).thenReturn(true);
		Mockito.when(userProfileRepo.getByAccountNumber(makeDepositPayload.getAcctNum()))
		                                    .thenReturn(userProfile);
		
		Mockito.when(userRepo.getByUsername(userCashier.getUsername())).thenReturn(userCashier);
		Mockito.when(userProfileRepo.save(userProfile)).thenReturn(userProfile);
		
		userCashier.setTransferPin(passwordEncoder.encode("1234"));
		
		BigDecimal depositAmt = new BigDecimal(makeDepositPayload.getAmount());
		BigDecimal userAcctBalBeforeDeposit = userProfile.getAccountBalance();
		userProfile.setAccountBalance(userProfile.getAccountBalance().add(depositAmt));
		BigDecimal userAcctBalAfterDeposit = userProfile.getAccountBalance();
		
		userService.makeDeposit(makeDepositPayload, userCashier.getUsername());
		
		verify(userProfileRepo, times(1)).getByAccountNumber(makeDepositPayload.getAcctNum());
		verify(userRepo, times(1)).getByUsername(userCashier.getUsername());
		verify(userProfileRepo, times(1)).save(userProfile);
		
		Assertions.assertNotEquals(userAcctBalBeforeDeposit,  userAcctBalAfterDeposit);
		Assertions.assertEquals(userAcctBalAfterDeposit,userAcctBalBeforeDeposit.add(depositAmt));
		
	}
	

}
