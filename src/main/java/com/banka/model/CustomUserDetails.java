package com.banka.model;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@SuppressWarnings("serial")
public class CustomUserDetails implements UserDetails{
	private Long id;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String email;
	private String username;
	private String accountNumber;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;
	
	

	public CustomUserDetails(Long id, String firstName, String lastName, String phoneNumber, String email, String username,
			String accountNumber, String password, Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.username = username;
		this.accountNumber = accountNumber;
		this.password = password;
		this.authorities = authorities;
	}
	
	public static CustomUserDetails grantedUser(User user) {
		List<GrantedAuthority> authorities = user.getRoles()
				.stream().map(role -> new SimpleGrantedAuthority(role.getName().name()))
				.collect(Collectors.toList());
		
		return new CustomUserDetails(
				user.getId(),
				user.getFirstName(),
				user.getLastName(),
				user.getPhoneNumber(),
				user.getEmail(),
				user.getUsername(),
				user.getAccountNumber(),
				user.getPassword(),
				authorities
				);
	}
	
	

	public Long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
