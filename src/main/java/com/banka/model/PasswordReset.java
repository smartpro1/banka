package com.banka.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
public class PasswordReset {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String resetToken;
	private LocalDateTime expiryDate;
	
	// OneToOne with UserProfile
		@OneToOne(fetch = FetchType.EAGER)
		@JoinColumn(nullable = false, name="user_id")
		@JsonIgnore
		private User user;
		
		

		public PasswordReset(String resetToken, LocalDateTime expiryDate, User user) {
		this.resetToken = resetToken;
		this.expiryDate = expiryDate;
		this.user = user;
	}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getResetToken() {
			return resetToken;
		}

		public void setResetToken(String resetToken) {
			this.resetToken = resetToken;
		}

		public LocalDateTime getExpiryDate() {
			return expiryDate;
		}

		public void setExpiryDate(LocalDateTime expiryDate) {
			this.expiryDate = expiryDate;
		}

		public User getUser() {
			return user;
		}

		public void setUserProfile(User user) {
			this.user = user;
		}
		
		

}
