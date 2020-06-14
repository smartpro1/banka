package com.banka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.banka.model.Transaction;

@Repository
public interface CashierRepository extends JpaRepository<Transaction, Long>{

}
