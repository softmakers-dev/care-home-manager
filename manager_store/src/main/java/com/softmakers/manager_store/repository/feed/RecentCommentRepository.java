package com.softmakers.manager_store.repository.feed;

import com.softmakers.manager_store.jpo.feed.Post;
import com.softmakers.manager_store.jpo.feed.RecentComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecentCommentRepository extends JpaRepository<RecentComment, Long> {

    @Query("select rc from RecentComment rc join fetch rc.comment where rc.post.postId = :id")
    List<RecentComment> findAllWithCommentByPostId(@Param("id") Long id);

    List<RecentComment> findAllByPost(Post post);

}
