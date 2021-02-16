package com.banka.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banka.model.CustomUserDetails;
import com.banka.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{
	List<Transaction> getByTransactionId(String transactionId);

	List<Transaction> getByStaffInvolved(String staffUsername);

	List<Transaction> getByUser(CustomUserDetails user);

}
