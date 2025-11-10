package com.softmakers.manager_store.repository.search;

import com.softmakers.manager_store.jpo.search.Search;
import com.softmakers.manager_store.repository.search.querydsl.SearchRepositoryQuerydsl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRepository extends JpaRepository<Search, Long>, SearchRepositoryQuerydsl {

}
