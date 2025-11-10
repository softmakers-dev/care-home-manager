package com.softmakers.manager_domain.store;

import com.softmakers.manager_domain.entity.feed.CommentDto;
import org.springframework.data.domain.Page;

public interface CommentStore {
    public CommentDto saveComment( CommentDto commentDto, Long parentId );
    public Page<CommentDto> findCommentsByPage( Long postId, int page );
    public Page<CommentDto> findReplyDtoPage( Long commentId, int page );
    public void likeComment( Long commentId );
}
