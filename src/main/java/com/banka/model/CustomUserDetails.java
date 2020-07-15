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
	private String fullname;
	private String sex;
	private String email;
	private String username;
	private String password;
	private String isActive = "registered";
	private Collection<? extends GrantedAuthority> authorities;
	
	public CustomUserDetails(Long id, String fullname, String sex, String email, String username,
			String password, Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.fullname = fullname;
		this.sex = sex;
		this.email = email;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}



	public static CustomUserDetails grantedUser(User user) {
		List<GrantedAuthority> authorities = user.getRoles()
				.stream().map(role -> new SimpleGrantedAuthority(role.getName().name()))
				.collect(Collectors.toList());
		
		return new CustomUserDetails(
				user.getId(),
				user.getFullname(),
				user.getSex(),
				user.getEmail(),
				user.getUsername(),
				user.getPassword(),
				authorities
				);
	}
	
	

	public Long getId() {
		return id;
	}

	public String getFullname() {
		return fullname;
	}

	public String getSex() {
		return sex;
	}


	public String getEmail() {
		return email;
	}

	

	public String getIsActive() {
		return isActive;
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
