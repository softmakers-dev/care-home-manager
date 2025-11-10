package com.softmakers.manager_store.repository.feed;

import com.softmakers.manager_store.jpo.UserJpo;
import com.softmakers.manager_store.jpo.feed.Bookmark;
import com.softmakers.manager_store.jpo.feed.Post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserAndPost(UserJpo user, Post post);
}
