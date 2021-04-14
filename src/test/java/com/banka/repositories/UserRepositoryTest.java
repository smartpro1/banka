package com.banka.repositories;

//import static org.junit.Assert.assertEquals;
//
//import org.junit.Assert;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//
//import com.banka.model.User;
//
//@DataJpaTest
//public class UserRepositoryTest {
//
//	@Autowired
//	private TestEntityManager entityManager;
//	
//	@Autowired
//	private UserRepository userRepo;
//	
//	User user;
//	@BeforeEach
//	public void init() {
//		user = new User("Akeni Promise", "M", "username", 
//	              "banka@yahoo.com", "password", "1234");
//		entityManager.persist(user);	
//	}
//	
//	@AfterEach
//	public void tearDown() {
//		entityManager.remove(user);
//	}
	
//	@Test
//	public void shouldGetUserById() {
//		User getUserById = userRepo.getById(1L);
//		Assert.assertNotNull(getUserById);
//		assertEquals(user, getUserById);
//	}
//	
//	@Test
//	public void shouldReturnUserByUsername() {
//		User getUser = userRepo.getByUsername("username"); 
//		Assert.assertNotNull(getUser);
//		assertEquals(user, getUser);
//	}
//	
//	@Test
//	public void shouldConfirmUserExistsByUsername() {
//		boolean isExist = userRepo.existsByUsername("username");
//		assertEquals(true, isExist);
//	}
//	
//	
//	@Test
//	public void shouldConfirmUserExistsByEmail() {
//		boolean isExist = userRepo.existsByEmail("banka@yahoo.com");
//		assertEquals(true, isExist);
//	}
//	
//	
//	@Test
//	public void shouldgetUserByUsernameOrEmail() {
//		User userByUsernameOrEmail = userRepo.getByUsernameOrEmail("username", "banka@yahoo.com");
//		Assert.assertNotNull(userByUsernameOrEmail);
//		assertEquals(user, userByUsernameOrEmail);
//	}
//	
//	@Test
//	public void shouldgetUserByEmail() {
//		User userByUsernameOrEmail = userRepo.getByEmail("banka@yahoo.com");
//		Assert.assertNotNull(userByUsernameOrEmail);
//		assertEquals(user, userByUsernameOrEmail);
//	}
	
	/*
	
	User getById(Long id);

	User getByUsernameOrEmail(String username, String email);

	User getByEmail(String email);
	 * 
	 * */
//}
