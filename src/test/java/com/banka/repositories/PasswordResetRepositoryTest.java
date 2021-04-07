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
import com.banka.model.User;
import com.banka.model.UserProfile;

@DataJpaTest
public class PasswordResetRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private PasswordResetRepository passwordResetRepo;
	
	
	PasswordReset passwordReset;
	User user;
	
	@BeforeEach
	public void init() {
		user = new User("Akeni Promise", "M", "username", 
	              "banka@yahoo.com", "password", "1234");
		
		entityManager.persist(user);
		
		passwordReset = new PasswordReset("a211*...", LocalDateTime.now().plusMinutes(20), user);
		entityManager.persist(passwordReset);
		
		System.out.println("Password Reset in init() is : " + passwordReset);
	}
	
	
	@Test
	public void shouldFindPasswordResetByToken() {
		PasswordReset getPasswordResetByToken = passwordResetRepo.findByResetToken("a211*...");
		Assertions.assertThat(getPasswordResetByToken).isEqualTo(passwordReset);
		assertEquals(passwordReset, getPasswordResetByToken);
	}
	
	@Test
	public void shouldGetPasswordResetByUserId() {		
		PasswordReset getPasswordResetByUserId = passwordResetRepo.getPasswordResetByUserId(user.getId());
		assertEquals(passwordReset, getPasswordResetByUserId);
	}
	
}







