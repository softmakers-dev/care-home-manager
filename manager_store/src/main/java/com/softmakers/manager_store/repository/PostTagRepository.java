package com.softmakers.manager_store.repository;

import com.softmakers.manager_domain.entity.feed.PostTagDto;
import com.softmakers.manager_store.jpo.feed.PostImage;
import com.softmakers.manager_store.jpo.feed.PostTag;
import com.softmakers.manager_store.repository.feed.jdbc.PostTagRepositoryJdbc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostTagRepository extends JpaRepository< PostTag, Long >, PostTagRepositoryJdbc {

    List<PostTag> findAllByPostImageIn( List<PostImage> postImages );

    @Query( "select new com.softmakers.manager_domain.entity.feed.PostTagDto("
            + "pt.postImage.id, pt.id, pt.tag.username )"
            + "from PostTag pt "
            + "where pt.postImage.id in :postImageIds" )
    List<PostTagDto> findAllPostTagDto( @Param( value = "postImageIds" ) List<Long> postImageIds );
}
