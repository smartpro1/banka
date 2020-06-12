package com.banka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banka.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
	
	boolean existsByPhoneNumber(String phoneNumber);

	User getByUsername(String username);

	User getById(Long id);

	User getByAccountNumber(String accountNumber);

	

}
