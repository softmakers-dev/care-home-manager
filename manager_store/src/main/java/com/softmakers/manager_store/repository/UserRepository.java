package com.softmakers.manager_store.repository;

import com.softmakers.manager_store.jpo.UserJpo;
import com.softmakers.manager_store.repository.feed.querydsl.MemberPostRepositoryQuerydsl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserJpo, BigDecimal>,
        MemberPostRepositoryQuerydsl {

    Optional<UserJpo> findUserByEmail(String email);
    Optional<UserJpo> findUserByUserName(String userName);
    List<UserJpo> findAllByUserNameIn( Collection<String> usernames );

    @Query("SELECT u FROM UserJpo u WHERE u.user_id IN :ids")
    List<UserJpo> findAllByUser_idIn(Collection<BigDecimal> ids);
}
