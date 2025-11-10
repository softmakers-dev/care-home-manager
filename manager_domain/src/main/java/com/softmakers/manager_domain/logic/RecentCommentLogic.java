package com.softmakers.manager_domain.logic;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.feed.CommentDto;
import com.softmakers.manager_domain.entity.feed.PostDto;
import com.softmakers.manager_domain.lifecycle.StoreLifecycle;
import com.softmakers.manager_domain.spec.RecentCommentService;
import com.softmakers.manager_domain.store.RecentCommentStore;

public class RecentCommentLogic implements RecentCommentService {

    private RecentCommentStore recentCommentStore;
    private final StoreLifecycle storeLifecycle;

    public RecentCommentLogic(StoreLifecycle storeLifecycle) {
        this.storeLifecycle = storeLifecycle;
        this.recentCommentStore = this.storeLifecycle.requestRecentCommentStore();
    }

    @Override
    public void deleteAll(PostDto postDto) {
        this.recentCommentStore.deleteAll( postDto );
    }

    @Override
    public void updateByUploadingComment( PostDto postDto, User user, CommentDto commentDto,
                                         Long parentId ) {
        this.recentCommentStore.updateByUploadingComment( postDto, user, commentDto, parentId );
    }
}
