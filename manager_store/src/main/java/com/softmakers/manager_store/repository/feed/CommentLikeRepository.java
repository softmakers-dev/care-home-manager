package com.softmakers.manager_store.repository.feed;

import com.softmakers.manager_store.jpo.UserJpo;
import com.softmakers.manager_store.jpo.feed.Comment;
import com.softmakers.manager_store.jpo.feed.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository< CommentLike, Long > {
    Optional<CommentLike> findByUserAndComment(UserJpo user, Comment comment);

    List<CommentLike> findAllByCommentIn(List<Comment> comments);
}
