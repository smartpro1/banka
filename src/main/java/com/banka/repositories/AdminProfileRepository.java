package com.banka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banka.model.AdminProfile;

@Repository
public interface AdminProfileRepository extends JpaRepository<AdminProfile, Long>{

}
