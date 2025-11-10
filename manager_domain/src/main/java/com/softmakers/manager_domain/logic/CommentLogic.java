package com.softmakers.manager_domain.logic;

import com.softmakers.manager_domain.entity.feed.CommentDto;
import com.softmakers.manager_domain.lifecycle.StoreLifecycle;
import com.softmakers.manager_domain.spec.CommentService;
import com.softmakers.manager_domain.store.CommentStore;
import org.springframework.data.domain.Page;

public class CommentLogic implements CommentService {
    private CommentStore commentStore;
    private final StoreLifecycle storeLifecycle;

    public CommentLogic(StoreLifecycle storeLifecycle) {
        this.storeLifecycle = storeLifecycle;
        this.commentStore = this.storeLifecycle.requestCommentStore();
    }

    @Override
    public CommentDto uploadComment(CommentDto commentDto, Long parentId) {
        return this.commentStore.saveComment( commentDto, parentId );
    }

    @Override
    public Page<CommentDto> getCommentsByPage(Long postId, int page) {
        return this.commentStore.findCommentsByPage( postId, page );
    }

    @Override
    public Page<CommentDto> getReplyDtoPage(Long commentId, int page) {
        return this.commentStore.findReplyDtoPage( commentId, page );
    }

    @Override
    public void likeComment(Long commentId) {
        this.commentStore.likeComment( commentId );
    }
}
