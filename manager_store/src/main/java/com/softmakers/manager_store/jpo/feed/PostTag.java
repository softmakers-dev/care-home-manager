package com.softmakers.manager_store.jpo.feed;

import com.querydsl.core.annotations.QueryInit;
import com.softmakers.manager_store.vo.Tag;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@Table(name = "post_tags")
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * @QueryInit : to resolve Querydsl's depth limit
     * @Ref https://github.com/querydsl/querydsl/issues/2129
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_image_id")
    @QueryInit({"*.*", "post.userJpo"})
    private PostImage postImage;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "y", column = @Column(name = "post_tag_y")),
            @AttributeOverride(name = "x", column = @Column(name = "post_tag_x")),
            @AttributeOverride(name = "username", column = @Column(name = "post_tag_username"))
    })
    private Tag tag;

    @Builder
    public PostTag(PostImage postImage, Tag tag) {
        this.postImage = postImage;
        this.tag = tag;
    }
}
