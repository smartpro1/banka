//package com.banka.repositories;
//
//import static org.junit.Assert.assertEquals;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.data.jpa.repository.Query;
//
//import com.banka.model.PasswordReset;
//import com.banka.model.Role;
//import com.banka.model.RoleName;
//import com.banka.model.User;
//import com.banka.model.UserProfile;
//
//@DataJpaTest
//public class RoleRepositoryTest {
//
//	@Autowired
//	private TestEntityManager entityManager;
//	
//	@Autowired
//	private RoleRepository roleRepo;
//		
//	@Test
//	public void shouldFindPasswordResetByToken() {
//		Role role = new Role(RoleName.ROLE_USER);
//		entityManager.persist(role);
//		Role getRoleByName = roleRepo.findByName(RoleName.ROLE_USER);
//		assertEquals(role, getRoleByName);
//	}
//	
//
//	
//}
//
//
//
//
//
//
//
