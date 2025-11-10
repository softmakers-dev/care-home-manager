package com.softmakers.manager_domain.spec;

import com.softmakers.manager_domain.entity.feed.CommentDto;
import org.springframework.data.domain.Page;

public interface CommentService {
    public CommentDto uploadComment(CommentDto commentDto, Long parentId );
    public Page<CommentDto> getCommentsByPage(Long postId, int page );
    public Page<CommentDto> getReplyDtoPage( Long commentId, int page );
    public void likeComment( Long commentId );
}
