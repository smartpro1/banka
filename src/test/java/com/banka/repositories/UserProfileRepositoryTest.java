package com.banka.repositories;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.banka.model.User;
import com.banka.model.UserProfile;

@DataJpaTest
public class UserProfileRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private UserProfileRepository userProfileRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	User user;
	UserProfile userProfile;
	
	@BeforeEach
	public void init() {
		user = new User("Akeni Promise", "M", "username", 
	              "banka@yahoo.com", "password", "1234");
		
		entityManager.persist(user);
		
		BigDecimal acctBal = new BigDecimal("0.00");
	    userProfile = new UserProfile("09062931318", "0212345678");
		userProfile.setUser(user);
		userProfile.setAccountBalance(acctBal);
		
		entityManager.persist(userProfile );	
	}
	
	
	@Test
	public void shouldConfirmUserProfileExistsByPhoneNumber() {
		boolean isExist = userProfileRepo.existsByPhoneNumber("09062931318");
		assertEquals(true, isExist);
	}
	
	@Test
	public void shouldGetUserProfileByAccountNumber() {
		UserProfile getSavedUserProfile = userProfileRepo.getByAccountNumber("0212345678");
		assertEquals(userProfile, getSavedUserProfile);
	}
	
	@Test
	public void shouldGetUserProfileByUserId() {
		UserProfile getSavedUserProfileById =
				      userProfileRepo.getUserProfileByUserId(userProfile.getId());
		assertEquals(userProfile, getSavedUserProfileById);
	}
}







