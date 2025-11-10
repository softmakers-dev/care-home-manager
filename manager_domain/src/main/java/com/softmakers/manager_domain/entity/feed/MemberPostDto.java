package com.softmakers.manager_domain.entity.feed;

import com.google.gson.annotations.Expose;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPostDto {

    private Long postId;

    private BigDecimal userId;
    private String userName;
    private PostImageDto postImage;
    private boolean hasManyPostImages;

    @Expose
    private boolean likeOptionFlag;

    @Expose
    private boolean postLikeFlag;
    private int postCommentsCount;
    private int postLikesCount;

    @Builder
    @QueryProjection
    public MemberPostDto(Long postId, BigDecimal userId, String userName, boolean hasManyPostImages,
                         boolean likeOptionFlag, boolean postLikeFlag,
                         int postCommentsCount, int postLikesCount ) {
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;

        this.hasManyPostImages = hasManyPostImages;
        this.likeOptionFlag = likeOptionFlag;
        this.postLikeFlag = postLikeFlag;
        this.postCommentsCount = postCommentsCount;
        this.postLikesCount = postLikesCount;
    }

    @Builder
    @QueryProjection
    public MemberPostDto(Long postId, BigDecimal userId, String userName, boolean hasManyPostImages, int postCommentsCount, int postLikesCount) {
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;

        this.hasManyPostImages = hasManyPostImages;
        this.postCommentsCount = postCommentsCount;
        this.postLikesCount = postLikesCount;
    }

    public void setPostImage(PostImageDto postImageDto) {
        this.postImage = postImageDto;
    }

    public void setPostLikesCount(int postLikesCount) {
        this.postLikesCount = postLikesCount;
    }
}
