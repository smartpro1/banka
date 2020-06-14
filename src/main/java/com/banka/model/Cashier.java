package com.banka.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

@Entity
public class Cashier extends Management{
    
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "cashiers_roles",
	    joinColumns = @JoinColumn(name = "cashier_id"),
	    inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();
	
	// OneToMany with Transactions
	@OneToMany(mappedBy = "cashier")
	List<Transaction> transactions = new ArrayList<>();

	public Cashier() {
	
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
