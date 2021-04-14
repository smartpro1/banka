//package com.banka.repositories;
//
//import static org.junit.Assert.assertEquals;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import com.banka.model.AdminProfile;
//import com.banka.model.PasswordReset;
//import com.banka.model.Role;
//import com.banka.model.RoleName;
//import com.banka.model.Transaction;
//import com.banka.model.User;
//import com.banka.model.UserProfile;
//
//@DataJpaTest
//public class TransactionRepositoryTest {
//
//	@Autowired
//	private TestEntityManager entityManager;
//	
//	@Autowired
//	private TransactionRepository transactionRepo;
//	
//	User user;
//	
//	@BeforeEach
//	private void init() {
//		
//		 user = new User("Akeni Promise", "M", "username", 
//	              "banka@yahoo.com", "password", "1234");
//		
//		entityManager.persist(user);
//		
//		BigDecimal acctBal = new BigDecimal("25000.00");
//	    UserProfile userProfile = new UserProfile("09062931318", "0212345678");
//		userProfile.setUser(user);
//		userProfile.setAccountBalance(acctBal);
//		
//		entityManager.persist(userProfile);
//		
//		User user2 = new User("Foo Bar", "M", "username2", 
//	              "mail@yahoo.com", "password2", "1234");
//		
//		entityManager.persist(user2);
//		
//		BigDecimal acctBal2 = new BigDecimal("25000.00");
//	    UserProfile userProfile2 = new UserProfile("08062931318", "0112345678");
//		userProfile.setUser(user2);
//		userProfile.setAccountBalance(acctBal2);
//		
//		entityManager.persist(userProfile2);
//	
//        String transactionId = "transactionId";
//        BigDecimal transferAmount = new BigDecimal("5000");
//        userProfile2.setAccountBalance(acctBal2.subtract(transferAmount));
//        userProfile.setAccountBalance(acctBal.add(transferAmount));
//        entityManager.persist(userProfile);
//        entityManager.persist(userProfile2);
//		
//        // user2 sends money to user1
//		Transaction transaction1 = new Transaction("CREDIT", transferAmount, userProfile2.getAccountNumber(), "money for food",
//				null,  user, transactionId);
//		
//		entityManager.persist(transaction1);
//		
//		Transaction transaction2 = new Transaction("DEBIT", transferAmount, userProfile.getAccountNumber(), "money for food",
//				null,  user2, transactionId);
//		
//		entityManager.persist(transaction2);
//				
//	}
//		
//	@Test
//	public void shouldGetTransactionById() {
//		List<Transaction> transactionsById = transactionRepo.getByTransactionId("transactionId");
//		assertEquals(2, transactionsById.size());
//	}
//	
//	@Test
//	public void shouldGetTransactionByStaffInvolved() {
//		User staff = new User("Akeni Promise", "M", "staffUsername", 
//	              "bankastaff@yahoo.com", "password", "1234");
//		
//		entityManager.persist(staff);
//		
//		Transaction withdrawalTransaction = new Transaction("DEBIT", new BigDecimal("4000"), null, "withdrawal",
//				staff.getUsername(),  user, "transactionId");
//		
//		entityManager.persist(withdrawalTransaction);
//		
//		List<Transaction> transactionsByStaff = transactionRepo.getByStaffInvolved("staffUsername");
//		assertEquals(1, transactionsByStaff.size());
//		assertEquals("staffUsername", transactionsByStaff.get(0).getStaffInvolved());
//	}
//	
//	@Test
//	public void shouldGetTransactionsByUser() {
//		User staff = new User("Akeni Promise", "M", "staffUsername", 
//	              "bankastaff@yahoo.com", "password", "1234");
//		
//		entityManager.persist(staff);
//		
//		Transaction depositTransaction = new Transaction("CREDIT", new BigDecimal("2500"), null, "withdrawal",
//				staff.getUsername(),  user, "transactionId");
//		
//		entityManager.persist(depositTransaction);
//		
//		List<Transaction> transactionsByUser = transactionRepo.getByUser(user);
//		assertEquals(2, transactionsByUser.size());
//		assertEquals(user, transactionsByUser.get(0).getUser());
//	}
//	
//	@Test
//	public void shouldGetTransactionsByUserId() {
//		List<Transaction> transactionsByUserId = transactionRepo.getByUserId(user.getId());
//		assertEquals(1, transactionsByUserId.size());
//	}
//	
//	@Test
//	public void shouldFindTransactionsByDateRange() {
//		String yesterday  = LocalDate.now().minusDays(1).toString();
//		String today = LocalDate.now().toString();
//		
//		Page<Transaction> findTransactionsBydateRange = transactionRepo.findByDateRange(yesterday, today, PageRequest.of(0, 3));
//	    assertEquals(3, findTransactionsBydateRange.getSize());
//	}
//	
//}
//
//
//
//
//
//
//
