package com.banka.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.banka.model.CustomUserDetails;
import com.banka.model.User;
import com.banka.model.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long>{

	boolean existsByPhoneNumber(String phoneNumber);

	UserProfile getByAccountNumber(String accountNumber);
	
	@Query(value="SELECT * FROM user_profile WHERE user_id =?1", nativeQuery=true)
    UserProfile getUserProfileByUserId(Long user_id);
	
	@Query(value="SELECT * FROM user_profile LIMIT 2", nativeQuery=true)
    List<UserProfile> getDummyAccounts();

	UserProfile getById(Long id);

	UserProfile getByUser(User uzer);

	UserProfile getByUser_id(Long id);

	UserProfile getByPhoneNumber(String string);

	boolean existsByAccountNumber(String accountNumber);

	

}
