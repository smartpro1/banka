package com.banka.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

@Entity
public class Admin extends Management{

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "adminss_roles",
	    joinColumns = @JoinColumn(name = "admin_id"),
	    inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();
	
	// OneToMany with Transactions
	@OneToMany(mappedBy = "admin")
	List<Transaction> transactions = new ArrayList<>();

	public Admin() {

	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	
	
	
}
