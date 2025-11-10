package com.softmakers.manager_domain.entity.feed;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDto {

    private transient Long postId;
    private Long id;
    private BigDecimal userId;
    private String userName;
    private String imageUrl;
    private String content;
    private LocalDateTime createdAt;
    private int commentLikesCount = 0;
    private boolean commentLikeFlag = false;
    private int repliesCount = 0;

    public CommentDto( Long id, BigDecimal userId, String userName, String imageUrl,
                       String content, LocalDateTime createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.content = content;
        this.createdAt = createdAt;
    }

    @QueryProjection
    public CommentDto( Long postId, Long id, BigDecimal userId, String userName,
                      String imageUrl, String content, LocalDateTime uploadDate,
                      int commentLikesCount, boolean commentLikeFlag, int repliesCount ) {
        this.postId = postId;
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.content = content;
        this.createdAt = uploadDate;
        this.commentLikesCount = commentLikesCount;
        this.commentLikeFlag = commentLikeFlag;
        this.repliesCount = repliesCount;
    }

    @QueryProjection
    public CommentDto( Long postId, Long id, BigDecimal userId, String userName,
                       String imageUrl, String content, LocalDateTime uploadDate,
                       int commentLikesCount, int repliesCount ) {
        this.postId = postId;
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.content = content;
        this.createdAt = uploadDate;
        this.commentLikesCount = commentLikesCount;
        this.repliesCount = repliesCount;
    }
}
