package com.softmakers.manager_store.repository;

import com.softmakers.manager_domain.entity.feed.PostImageDto;
import com.softmakers.manager_store.jpo.feed.Post;
import com.softmakers.manager_store.jpo.feed.PostImage;
import com.softmakers.manager_store.repository.feed.jdbc.PostImageRepositoryJdbc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long>,
        PostImageRepositoryJdbc {

    List<PostImage> findAllByPost(Post post);

    @Query("select new com.softmakers.manager_domain.entity.feed.PostImageDto("
            + "pi.post.postId, pi.id, pi.image.imageUrl, pi.altText) "
            + "from PostImage pi "
            + "where pi.post.postId in :postIds")
    List<PostImageDto> findAllPostImageDtoByPostIdIn(@Param(value = "postIds") List<Long> postIds);

    @Query("select new com.softmakers.manager_store.jpo.feed.PostImage("
            + "pi.post.postId, pi.id, pi.image.imageUrl, pi.altText) "
            + "from PostImage pi "
            + "where pi.post.postId in :postIds")
    List<PostImage> findAllByPostIdIn(List<Long> postIds);
}
