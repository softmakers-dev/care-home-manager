package com.softmakers.manager_domain.entity.feed;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PostDto {

    private Long postId;
    private String userName;
    private BigDecimal userId;
    private String imageUrl;
    private Long boardId;
    private String title;
    private String content;
    private Timestamp createdAt;

    private long postCommentsCount;
    private long postLikesCount;
    private boolean likeOptionFlag;
    private boolean commentOptionFlag;
    private List<CommentDto> recentComments = new ArrayList<>();

    private List<PostImageDto> postImages = new ArrayList<>();

    @QueryProjection
    public PostDto( Long postId, String content, Timestamp createdAt,
                    String userName, BigDecimal userId, String imageUrl,
                    int postCommentsCount, int postLikesCount,
                    boolean likeFlag, boolean commentFlag ) {

        this.postId = postId;
        this.content = content;
        this.createdAt = createdAt;
        this.userName = userName;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.postCommentsCount = postCommentsCount;
        this.postLikesCount = postLikesCount;
        this.likeOptionFlag = likeFlag;
        this.commentOptionFlag = commentFlag;
    }

    public void setRecentComments(List<CommentDto> commentDtos) {
        this.recentComments = commentDtos;
    }

    public void setPostImages(List<PostImageDto> postImageDtos) {
        this.postImages = postImageDtos;
    }

    public void setPostLikesCount(long postLikesCount) {
        this.postLikesCount = postLikesCount;
    }
}
