package com.softmakers.manager_store.repository;

import com.softmakers.manager_store.jpo.UserJpo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserJpo, BigDecimal> {
    Optional<UserJpo> findUserByEmail(String email);
    Optional<UserJpo> findUserByUserName(String userName);
}
