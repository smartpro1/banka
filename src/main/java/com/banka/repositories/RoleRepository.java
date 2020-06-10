package com.banka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banka.model.Role;
import com.banka.model.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

	Role findByName(RoleName user);

}
