package com.softmakers.manager_store.repository.feed;

import com.softmakers.manager_store.jpo.UserJpo;
import com.softmakers.manager_store.jpo.feed.Post;
import com.softmakers.manager_store.jpo.feed.PostLike;
import com.softmakers.manager_store.repository.feed.querydsl.PostLikeRepositoryQuerydsl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long>,
        PostLikeRepositoryQuerydsl {
    List<PostLike> findAllByPost(Post post);
    Optional<PostLike> findByUserAndPost(UserJpo user, Post post);
}
