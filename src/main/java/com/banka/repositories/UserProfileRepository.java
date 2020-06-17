package com.banka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banka.model.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long>{

	boolean existsByPhoneNumber(String phoneNumber);

	UserProfile getByAccountNumber(String accountNumber);

}
