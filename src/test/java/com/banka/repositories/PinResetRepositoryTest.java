package com.banka.repositories;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.Query;

import com.banka.model.PasswordReset;
import com.banka.model.PinReset;
import com.banka.model.Role;
import com.banka.model.RoleName;
import com.banka.model.User;
import com.banka.model.UserProfile;

@DataJpaTest
 class PinResetRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private PinResetRepository pinResetRepo;
	
	private User user;
	private PinReset pinReset;
//	private UserProfile userProfile;
	
	@BeforeEach
	private void init() {
		user = new User("Akeni Promise", "M", "username", 
	              "banka@yahoo.com", "password", "1234");
		entityManager.persist(user);
		
//		BigDecimal acctBal = new BigDecimal("0.00");
//	    userProfile = new UserProfile("09062931318", "0212345678");
//		userProfile.setUser(user);
//		userProfile.setAccountBalance(acctBal);
//		
//		entityManager.persist(userProfile );	
		
		
		pinReset = new PinReset("ay2ww...", LocalDateTime.now().plusMinutes(20), user);
		
		entityManager.persist(pinReset );	
	}
		
	@Test
	private void shouldFindPinResetByToken() {
		PinReset findPinResetByToken = pinResetRepo.findByResetToken("ay2ww...");
		assertEquals(pinReset, findPinResetByToken);
	}
	
	@Test
	private void shouldGetPinResetByToken() {
		PinReset getByResetToken = pinResetRepo.getByResetToken("ay2ww...");
		assertEquals(pinReset, getByResetToken);
	}
	
//	@Test
//	public void shouldGetPinResetByUserId() {
//		PinReset getPinResetByUserProfileId = pinResetRepo.getPinResetByUserProfileId(userProfile.getId());
//		assertEquals(pinReset, getPinResetByUserProfileId);
//	}
	
	/*
	 * PinReset findByResetToken(String token);
	
	@Query(value="SELECT * FROM pin_reset WHERE user_profile_id =?1", nativeQuery=true)
    PinReset getPinResetByUserProfileId(Long user_Profile_id);

	PinReset getByResetToken(String confirmationToken);
	 */
	

	
}







