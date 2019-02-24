package com.putracode.baeldung.security.registration.persistence.dao;

import com.putracode.baeldung.security.registration.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Override
    void delete(User user);

}