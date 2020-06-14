package com.banka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banka.model.Admin;

@Repository
public interface TransactionRepository extends JpaRepository<Admin, Long>{

}
