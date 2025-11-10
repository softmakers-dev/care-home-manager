package com.softmakers.manager_store.repository.feed;

import com.softmakers.manager_store.jpo.feed.Comment;
import com.softmakers.manager_store.jpo.feed.Post;
import com.softmakers.manager_store.repository.feed.querydsl.CommentRepositoryQuerydsl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository< Comment, Long >,
        CommentRepositoryQuerydsl {

    List<Comment> findAllByPost(Post post);
    Optional<Comment> findWithPostAndUserById( @Param("id") Long id );
}
