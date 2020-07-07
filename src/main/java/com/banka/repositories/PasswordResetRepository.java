package com.banka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.banka.model.PasswordReset;


@Repository
public interface PasswordResetRepository  extends JpaRepository<PasswordReset, Long>{
	PasswordReset findByResetToken(String token);
	
	@Query(value="SELECT * FROM password_reset WHERE user_id =?1", nativeQuery=true)
    PasswordReset getPasswordResetByUserId(Long user_id);
}
