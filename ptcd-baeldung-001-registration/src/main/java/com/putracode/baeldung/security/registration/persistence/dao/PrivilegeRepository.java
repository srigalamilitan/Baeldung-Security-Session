package com.putracode.baeldung.security.registration.persistence.dao;

import com.putracode.baeldung.security.registration.persistence.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegeRepository extends JpaRepository<Privilege,Long> {
    Privilege findByName(String name);

    @Override
    void delete(Privilege privilege);
}
