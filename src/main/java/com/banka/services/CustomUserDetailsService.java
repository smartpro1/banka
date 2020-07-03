package com.banka.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.banka.exceptions.CredentialNotFoundException;
import com.banka.model.CustomUserDetails;
import com.banka.model.User;
import com.banka.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepo;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.getByUsername(username);
		if(user == null) throw new CredentialNotFoundException("invalid username or password");
		
		return CustomUserDetails.grantedUser(user);
	}
	
	// This method is used by JwtAuthenticationFilter
	public UserDetails loadUserById(Long id) {
		User user = userRepo.getById(id);
		if(user == null) throw new CredentialNotFoundException("invalid username or password");
		
		return CustomUserDetails.grantedUser(user);
	}

}
