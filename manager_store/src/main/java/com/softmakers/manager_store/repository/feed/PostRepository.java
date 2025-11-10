package com.softmakers.manager_store.repository.feed;

import com.softmakers.manager_store.jpo.feed.Post;
import com.softmakers.manager_store.repository.feed.querydsl.PostRepositoryQuerydsl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryQuerydsl {
    List<Post> findTop10ByOrderByPostIdDesc();
}
