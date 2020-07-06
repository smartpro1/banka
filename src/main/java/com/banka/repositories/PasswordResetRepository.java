package com.banka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banka.model.PasswordReset;


@Repository
public interface PasswordResetRepository  extends JpaRepository<PasswordReset, Long>{
	PasswordReset findByResetToken(String token);
}
