package com.softmakers.manager_store.jpo.feed;

import com.softmakers.manager_domain.entity.feed.CommentDto;
import com.softmakers.manager_store.jpo.UserJpo;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@Table( name = "comments" )
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = "id" )
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private UserJpo user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Lob
    @Column(name = "content")
    private String content;

    @CreatedDate
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "comment")
    private List<CommentLike> commentLikes = new ArrayList<>();

    @Builder
    public Comment( Comment parent, UserJpo user, Post post, String content, LocalDateTime createdAt ) {
        this.parent = parent;
        this.user = user;
        this.post = post;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Comment( CommentDto commentDto ) {
        this.user = new UserJpo();
        this.user.setUser_id( commentDto.getUserId() );
        this.post = new Post();
        this.post.setPostId( commentDto.getPostId() );

        this.setId( commentDto.getId() );
        this.content = commentDto.getContent();
        this.createdAt = commentDto.getCreatedAt();
    }
}
