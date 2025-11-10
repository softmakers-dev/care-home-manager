package com.softmakers.manager_store.jpo.feed;

import com.softmakers.manager_domain.entity.feed.PostDto;
import com.softmakers.manager_store.jpo.UserJpo;
import com.softmakers.manager_store.vo.Image;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

//    @Column(name = "user_id")
//    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserJpo userJpo;

    @Column(name = "board_id")
    private Long boardId;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "modified_at")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostLike> postLikes = new ArrayList<>();

    @Column(name = "post_comment_flag")
    private boolean commentFlag;

    @Column(name = "post_like_flag")
    private boolean likeFlag;

    @OneToMany(mappedBy = "post")
    private List<PostImage> postImages = new ArrayList<>();

    public Post( PostDto postDto ) {

        BeanUtils.copyProperties( postDto, this );
        this.userJpo = new UserJpo();
        this.userJpo.setUser_id( postDto.getUserId() );
        this.userJpo.setUserName( postDto.getUserName() );
        Image userImage = new Image();
        userImage.setUrl( postDto.getImageUrl() );
        this.userJpo.setImage( userImage );
        this.setLikeFlag( postDto.isLikeOptionFlag() );
        this.setCommentFlag( postDto.isCommentOptionFlag() );
    }

    public PostDto toDomain() {
        PostDto postDto = new PostDto();
        BeanUtils.copyProperties(this, postDto);
        return postDto;
    }

    @Builder
    public Post( UserJpo userJpo, String content, boolean commentFlag, boolean likeFlag ) {
        this.userJpo = userJpo;
        this.content = content;
        this.commentFlag = commentFlag;
        this.likeFlag = likeFlag;
    }
}
