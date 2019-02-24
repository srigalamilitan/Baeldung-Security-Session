package com.putracode.baeldung.security.registration.persistence.dao;

import com.putracode.baeldung.security.registration.persistence.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

    @Override
    void delete(Role role);
}