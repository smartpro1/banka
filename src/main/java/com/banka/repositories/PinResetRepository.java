package com.banka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.banka.model.PasswordReset;
import com.banka.model.PinReset;


@Repository
public interface PinResetRepository  extends JpaRepository<PinReset, Long>{
	PinReset findByResetToken(String token);
	
	@Query(value="SELECT * FROM pin_reset WHERE user_profile_id =?1", nativeQuery=true)
    PinReset getPinResetByUserProfileId(Long user_Profile_id);

	PinReset getByResetToken(String confirmationToken);

	PinReset getByUserId(Long id);
}
