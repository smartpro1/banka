package com.banka.services;

import static com.banka.model.RoleName.ROLE_USER;
import static com.banka.utils.GenTransactionId.generateTransactionId;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;

import com.banka.model.PinReset;
import com.banka.model.Role;
import com.banka.model.Transaction;
import com.banka.model.TransactionType;
import com.banka.model.User;
import com.banka.model.UserProfile;
import com.banka.payloads.UserRegPayload;
import com.banka.repositories.PinResetRepository;
import com.banka.repositories.TransactionRepository;
import com.banka.repositories.UserProfileRepository;
import com.banka.repositories.UserRepository;

//@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ServiceImplTest {

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
	
	
	@Test
	@DisplayName("Register User Test")
	public void shouldReturnRegisteredUser() {
		UserRegPayload userRegPayload = new UserRegPayload();
		userRegPayload.setFullname("Akeni Promise");
		userRegPayload.setSex("M");
		userRegPayload.setPhoneNumber("07062916111");
		userRegPayload.setEmail("promise@yahoo.com");
		userRegPayload.setUsername("username");
		userRegPayload.setPassword("password");
		
		User user = new User(userRegPayload.getFullname(), userRegPayload.getSex(), userRegPayload.getUsername(), 
				              userRegPayload.getEmail(), userRegPayload.getPassword(), "1234");
		
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
		
		SimpleMailMessage mailForActivation = new SimpleMailMessage();
		
		when(userRepo.save(user)).thenReturn(user);
		when(userProfileRepo.save(userProfile)).thenReturn(userProfile);
		when(pinResetRepo.save(pinReset)).thenReturn(pinReset);
		when(transactionRepo.save(bonusTransaction)).thenReturn(bonusTransaction);
	
//		when(transactionRepo.save(bonusTransaction)).thenReturn(bonusTransaction);
	//	when(emailService.sendEmail(mailForActivation)).thenReturn(mailForActivation);
		
		User returnedUser = userService.registerUser(userRegPayload, httpServletRequest);
		assertEquals(user.getUsername(), returnedUser.getUsername());
	}
	
//	private void createRegistrationBonus(UserProfile userProfile) {
//		BigDecimal bonus = new BigDecimal("25000.00");
//		String transactionId = generateTransactionId();
//		userProfile.setAccountBalance(bonus);
//		userProfileRepo.save(userProfile);
//		Transaction bonusTransaction = new Transaction(TransactionType.REG_BONUS.name(), bonus, userProfile.getAccountNumber(), 
//                "registration bonus",null, null, transactionId);
//		transactionRepo.save(bonusTransaction);
//	}
}
