package com.softmakers.manager_store.repository.search;

import com.softmakers.manager_store.jpo.search.SearchUserJpo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SearchUserRepository extends JpaRepository<SearchUserJpo, Long> {

    Optional<SearchUserJpo> findByUserJpoUserName(String username);
}
