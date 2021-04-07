package com.banka.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.banka.model.CustomUserDetails;
import com.banka.model.PinReset;
import com.banka.model.Transaction;
import com.banka.model.User;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{
	List<Transaction> getByTransactionId(String transactionId);

	List<Transaction> getByStaffInvolved(String staffUsername);

	List<Transaction> getByUser(User uzer);
	
	@Query(value="SELECT * FROM transaction WHERE user_id = :userId ORDER BY created_at DESC LIMIT 5 ", nativeQuery=true)
	List<Transaction> getByUserId(@Param("userId") Long userId);

	@Query(value="SELECT * FROM transaction WHERE created_at BETWEEN ?1 AND ?2", nativeQuery=true)
	Page<Transaction> findByDateRange(String start, String endDayStr, Pageable pageable);

}
