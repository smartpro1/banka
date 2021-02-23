package com.banka.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.banka.model.CustomUserDetails;
import com.banka.model.Transaction;
import com.banka.model.User;
import com.banka.model.UserProfile;
import com.banka.payloads.TransactionDto;
import com.banka.repositories.TransactionRepository;
import com.banka.repositories.UserProfileRepository;
import com.banka.repositories.TransactionRepository;
import com.banka.repositories.UserRepository;
import com.banka.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import static com.banka.security.SecurityConstants.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenProvider {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserProfileRepository userProfileRepo;
	
	@Autowired
	private TransactionRepository transactionRepo;
	
	@Autowired
	private UserRepository userRepo;

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
	
	public String generateJwtToken(Authentication authentication) {
		
		CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
		User uzer = userRepo.getByUsername(user.getUsername());
		UserProfile userProfile = userProfileRepo.getByUser(uzer);
		String acctNum = userProfile.getAccountNumber();
		BigDecimal acctBal = userProfile.getAccountBalance();
		List<TransactionDto> transactions = userService.getTransactionsByUserId(String.valueOf(uzer.getId()));
		
		String userId = Long.toString(user.getId());
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", userId);
		claims.put("username", user.getUsername());
		claims.put("email", user.getEmail());
		claims.put("fullname", user.getFullname());
		claims.put("acctNum", acctNum);
		claims.put("acctBal", acctBal);
		claims.put("transactions", transactions);
		
		
		String jws = Jwts.builder()
				.setSubject(userId)
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, SECRET)
				.compact();
		
		return jws;
	}
	
	// validate jwtToken
	public boolean validateJwtToken(String token) {
		try {
			Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
			return true;
		} catch (SignatureException ex) {
			logger.error("Invalid JWT Signature: " + ex);
		}catch (MalformedJwtException  ex) {
			logger.error("Malformed JWT Token: " + ex);
		}catch (ExpiredJwtException ex) {
			logger.error("Expired JWT Token: " + ex);
		}catch (UnsupportedJwtException ex) {
			logger.error("Unsupported JWT Token: " + ex);
		}catch (IllegalArgumentException ex) {
			logger.error("JWT claims string is empty: " + ex);
		}
		return false;
	}
	
	public Long getUserIdFromJwtToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
		String userId = (String) claims.get("id");
		return Long.parseLong(userId);
	}
}
