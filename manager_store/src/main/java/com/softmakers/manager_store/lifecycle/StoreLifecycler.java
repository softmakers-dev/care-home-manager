package com.softmakers.manager_store.lifecycle;

import com.softmakers.manager_domain.lifecycle.StoreLifecycle;
import com.softmakers.manager_domain.store.*;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreLifecycler implements StoreLifecycle {

    private final UserStore userStore;
    private final RefreshTokenStore refreshTokenStore;
    private final RegisterCodeRedisStore registerCodeRedisStore;
    private final PostStore postStore;
    private final CommentStore commentStore;
    private final RecentCommentStore recentCommentStore;
    private final MemberPostStore memberPostStore;
    private final ChatStore chatStore;
    private final SearchStore searchStore;

    @Override
    public UserStore requestUserStore() {
        return this.userStore;
    }

    @Override
    public RefreshTokenStore requestRefreshTokenStore() {
        return this.refreshTokenStore;
    }

    @Override
    public RegisterCodeRedisStore requestRegisterCodeRedisStore() {
        return this.registerCodeRedisStore;
    }

    @Override
    public PostStore requestPostStore() {
        return this.postStore;
    }

    @Override
    public CommentStore requestCommentStore() {
        return this.commentStore;
    }

    @Override
    public RecentCommentStore requestRecentCommentStore() {
        return this.recentCommentStore;
    }

    @Override
    public MemberPostStore requestMemberPostStore() {
        return this.memberPostStore;
    }

    @Override
    public ChatStore requestChatStore() {
        return this.chatStore;
    }

    @Override
    public SearchStore requestSearchStore() {
        return this.searchStore;
    }
}
